#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <pthread.h>
#include "timer.h"
const int TAM = 10;

typedef struct s1
{
    //Nesse caso, tarefa é a seção do vetor na qual a thread agirá
    int inicio, fim;

} tarefa;

typedef struct s2
{
    tarefa *valor;
    struct s2 *prox;
} tno;
int *vetor_concorrente, *vetor_sequencial, flag1 = 1, flag2 = 1, new_count = 0;
double inicio_tempo, fim_tempo;
pthread_mutex_t lock, lock2;
pthread_cond_t condicional;
tno *head, *inicio_lista, *fim_lista;
tarefa *inicializar_tarefa(int inicio, int fim)
{
    tarefa *novo;
    novo = malloc(sizeof(tarefa *));
    novo->inicio = inicio;
    novo->fim = fim;
    return novo;
}
tno *inicializar_lista()
{
    tno *novo;
    novo = malloc(sizeof(tno *));
    novo->prox = NULL;
    novo->valor = NULL;
    inicio_lista = novo;
    fim_lista = novo;
    return novo;
}
void insere(int inicio, int fim)
{
    tno *novo_no;
    tarefa *novo;
    novo = inicializar_tarefa(inicio, fim);
    novo_no = malloc(sizeof(tno *));
    novo_no->valor = novo;
    novo_no->prox = NULL;
    fim_lista->prox = novo_no;
    fim_lista = fim_lista->prox;
    new_count++;
}
tarefa *retira()
{
    tarefa *saida = inicio_lista->prox->valor != NULL ? inicio_lista->prox->valor : NULL;
    inicio_lista = inicio_lista->prox;
    new_count--;
    return saida;
}
int is_vazio()
{
    if (new_count == 0)
    {
        return 1;
    }
    else
    {
        return 0;
    }
}
void preencher_vetor(int flag)
{
    switch (flag)
    {
    case 0:
        srand(time(NULL));
        for (size_t i = 0; i < TAM; i++)
        {
            vetor_concorrente[i] = rand() % 100;
        }
        break;
    case 1:
        for (size_t i = 0; i < TAM; i++)
        {
            vetor_concorrente[i] = TAM - i;
        }
        break;
    case 2:
        for (size_t i = 0; i < TAM; i++)
        {
            vetor_concorrente[i] = i;
        }
        break;
    case 3:
        for (size_t i = 0; i < TAM; i++)
        {
            vetor_concorrente[i] = rand() % 100;
        }
        break;
    default:
        puts("ERRO-FLAG INVÁLIDA");
        break;
    }
    for (size_t i = 0; i < TAM; i++)
    {
        vetor_sequencial[i] = vetor_concorrente[i];
    }
}
void print_vetor()
{
    for (size_t i = 0; i < TAM; i++)
    {
        printf("%d ", vetor_concorrente[i]);
    }
    puts("");
}
void *compara()
{
    for (size_t i = 0; i < TAM; i++)
    {
        if (vetor_concorrente[i] != vetor_sequencial[i])
        {
            puts("Ordenacao incorreta");
            return NULL;
        }
    }
    puts("Ordenacao correta");
}
void troca(int *vetor_atual, int a_p, int b_p)
{
    int hold;
    hold = vetor_atual[a_p];
    vetor_atual[a_p] = vetor_atual[b_p];
    vetor_atual[b_p] = hold;
}
int quicksort_sequencial(int inicio, int fim)
{

    int pivo, pivo_p, i, j;
    pivo_p = (inicio + fim) / 2;
    pivo = vetor_sequencial[pivo_p];
    i = inicio;
    j = fim - 1;

    while (i <= j)
    {
        while (vetor_sequencial[i] < pivo && i < fim)
        {
            i++;
        }
        while (vetor_sequencial[j] > pivo && j > inicio)
        {
            j--;
        }
        if (i <= j)
        {
            troca(vetor_sequencial, i, j);
            i++;
            j--;
        }
    }
    if (j > inicio)
    {

        quicksort_sequencial(inicio, j + 1);
    }
    if (i < fim)
    {

        quicksort_sequencial(i, fim);
    }
}
tarefa *quicksort(int inicio, int fim, long int id)
{
    int pivo, pivo_p, i, j;
    tarefa *saida;
    pivo_p = (inicio + fim) / 2;
    pivo = vetor_concorrente[pivo_p];
    i = inicio;
    j = fim - 1;
    while (i <= j)
    {
        while (vetor_concorrente[i] < pivo && i < fim)
        {
            i++;
        }
        while (vetor_concorrente[j] > pivo && j > inicio)
        {
            j--;
        }
        if (i <= j)
        {
            troca(vetor_concorrente, i, j);
            i++;
            j--;
        }
    }
    //printf("Thread %ld i: %d j: %d\n", id, i, j);
    saida = inicializar_tarefa(i, j);
    return saida;
}
void *threads(void *arg)
{
    long int id = (long int)arg;

    while ((flag1 || flag2) || !is_vazio())
    {
      
        tarefa *atual, *prox;
        pthread_mutex_lock(&lock);
        while (is_vazio())
        {

            if (!(flag1 || flag2))
            {
                pthread_cond_broadcast(&condicional);
                pthread_exit(NULL);
            }

            pthread_cond_wait(&condicional, &lock);
        }
        atual = retira();
        pthread_mutex_unlock(&lock);
        
        prox = quicksort(atual->inicio, atual->fim, id);
        if (prox != NULL)
        {
            if (prox->fim > atual->inicio)
            {
                pthread_mutex_lock(&lock2);
                insere(atual->inicio, prox->fim + 1);
                pthread_mutex_unlock(&lock2);
                pthread_cond_signal(&condicional);
            }
            else
            {
                // puts("okay 1");
                // printf("%d\n ", new_count);
                flag1 = 0;
                // pthread_cond_signal(&condicional);
            }

            if (prox->inicio < atual->fim)
            {
                pthread_mutex_lock(&lock2);
                insere(prox->inicio, atual->fim);
                pthread_mutex_unlock(&lock2);
                pthread_cond_signal(&condicional);
            }
            else
            {
                // puts("ok 2");
                // printf("%d\n ", new_count);
                flag2 = 0;
                // pthread_cond_signal(&condicional);
            }
        }
        free(atual);
    }
    pthread_cond_broadcast(&condicional);
    pthread_exit(NULL);
}

int main(int argc, char const *argv[])
{
    pthread_t tids[4];
    vetor_concorrente = (int *)malloc(TAM * sizeof(int));
    vetor_sequencial = (int *)malloc(TAM * sizeof(int));
    preencher_vetor(1);
    quicksort_sequencial(0, TAM);
    GET_TIME(inicio_tempo);
    head = inicializar_lista();
    insere(0, TAM);
    pthread_mutex_init(&lock, NULL);
    pthread_mutex_init(&lock2, NULL);
    pthread_cond_init(&condicional, NULL);

  
    for (size_t i = 0; i < 4; i++)
    {
        if (pthread_create(&tids[i], NULL, threads, (void *)i))
        {
            puts("ERRO-CREATE");
            return 1;
        }
    }

    for (size_t i = 0; i < 4; i++)
    {
        if (pthread_join(tids[i], NULL))
        {
            puts("ERRO - JOIN");
            return 1;
        }
    }

    pthread_mutex_destroy(&lock);
    pthread_mutex_destroy(&lock2);
    pthread_cond_destroy(&condicional);
    GET_TIME(fim_tempo)
    compara();
    printf("%lf segundos\n", fim_tempo - inicio_tempo);
    return 0;
}
