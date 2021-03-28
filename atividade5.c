#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#define TAM 1000 // tamanho do vetor
#define NTHREADS 2// número de threads criadas além da principal

typedef struct 
{
    // dados passados para as threads secundárias
    int inicio, fim;
} data;

int *vetor; //vetor a ser incrementado
void preenche(int valor){
    //preenche o vetor com o valor passado
    for (size_t i = 0; i < TAM; i++)
    {
        vetor[i] = valor;
    }
}
void incrementa(int inicio, int fim){
    //incrementa em um os elementos do vetor contidos entre as posições inicio e fim
    for (size_t i = inicio; i <= fim; i++)
    {
        vetor[i]++;
    }
}
int verifica(int esperado){
    //verifica se todos os elementos do vetor possuem o valor esperado
    for (size_t i = 0; i < TAM; i++)
    {
        if (vetor[i]!=esperado)
        {
            printf("Fracasso! Posição %d diferente do esperado\n", (int)i);
            return -1;
        }
    }
    puts("Sucesso");
    return 0;
}
void * tarefa(void *arg){
    // tarefa a ser realizada pelas threads secundárias
    data *argumentos = (data *)arg;// recebe os parâmetros
    incrementa(argumentos->inicio, argumentos->fim);//chama a função de incrementação do pedaço designado ao vetor
    free(argumentos);//desaloca a struct
    pthread_exit(NULL);
}
int main(int argc, char const *argv[])
{
    vetor = malloc(TAM * sizeof(int));//alocação de memória para o vetor
    preenche(0);//preenche o vetor com zeros
    pthread_t tids[NTHREADS];//identificadores das threads
    data **argumentos;//vetor com argumentos a serem passados para as threads
    argumentos = malloc(NTHREADS*sizeof(data*));
    for (size_t i = 0; i < NTHREADS; i++)
    {
        argumentos[i] = malloc(sizeof(data*));// alocação de memória para struct
        argumentos[i]->inicio = i * (TAM / 2) + i;//define o inicio da parte do vetor que a thread lidara
        argumentos[i]->fim = (i + 1) * TAM / 2;//define o limite da parte do vetor que a thread lidara
        if(pthread_create(&tids[i], NULL, tarefa, (void *)argumentos[i])){
            //cria a thread
            puts("ERRO");
            return -1;
        }
        if (pthread_join(tids[i], NULL))
        {
            //garante que a main só continuara após o termino das threads secundárias
            puts("ERRO");
            return -1;
        }
    }
    verifica(1);//verifica se o vetor foi incrementado
    free(argumentos);//desaloca o vetor
    return 0;
}
