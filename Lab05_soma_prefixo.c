#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

int *vetor, TAM, count = 0, passo = 1, exp_passo = 0; //Respectivamente o vetor de números, o tamanho do vetor e o número de threads, a quantidade de threads na barreira, a distancia entre a posição da thread e onde a varpiavel auxiliar deve ir e o expoente do próximo passo
pthread_cond_t cond_barreira;                         //Variavel condicional para barreira
pthread_mutex_t lock_barreira;                        //Variavel de exclusão mútua
//Eleva uma base a um expoente
int potencia(int base, int exp)
{
    int saida = 1;
    for (size_t i = 0; i < exp; i++)
    {
        saida *= base;
    }
    return saida;
}
//Preeche o vetor com 1's
void preecher()
{
    for (size_t i = 0; i < TAM; i++)
    {
        vetor[i] = 1;
    }
}
//Imprime o vetor
void print_vetor()
{
    for (size_t i = 0; i < TAM; i++)
    {
        printf("%d ", vetor[i]);
    }
    puts("");
}
//Verifica se a soma foi feita corretamente
void *verifica()
{
    for (size_t i = 0; i < TAM; i++)
    {
        if (vetor[i] != i + 1)
        {
            puts("Soma incorreta");
            return NULL;
        }
    }
    puts("Soma correta");
}
//Garante que todas as threads estejam no mesmo ponto antes de continuar com a execução
void barreira(int flag)
{
    pthread_mutex_lock(&lock_barreira); //Inicio da seção crítica
    count++;
    if (count == TAM - passo) //Se a thread é a última a chegar na barreira desbloqueia o resto
    {
        pthread_cond_broadcast(&cond_barreira);
        count = 0; //Reseta o contador
        if (flag)  //Se a barreira está sendo chamada pela segunda vez incrementa o passo
        {
            exp_passo++;
            passo = potencia(2, exp_passo); //Passo é sempre uma potencia de 2
        }
    }
    else //Se não é a última thread, é bloqueada
    {

        pthread_cond_wait(&cond_barreira, &lock_barreira);
    }

    pthread_mutex_unlock(&lock_barreira); //Fim da seção crítica
}
void *tarefa(void *arg)
{
    long int id; // Posição do vetor pela qual a thread é responsável
    int hold;    // variável auxiliar
    id = (long int)arg;

    while (id - passo >= 0) //Se a thread for acessar uma posição inexistente, temina
    {
        hold = vetor[id - passo];
        barreira(0); //Garante que todos realizaram a leitura antes de mover para o próximo passo
        vetor[id] += hold;
        barreira(1); //Garante que todos realizaram a escrita antes de mover para o próximo passo
    }
}
int main(int argc, char const *argv[])
{
    int exp;         //Expoente que indica o tamanho do vetor e o número de threads
    pthread_t *tids; //Identificadores das threads
    if (argc < 2)    //Verifica se foram passados argumentos suficientes para o programa
    {
        puts("Argumentos Insuficientes");
        return 1;
    }
    exp = atoi(argv[1]);                    //Converte o que foi passado pela linha de comando para int
    TAM = potencia(2, exp);                 //Vetor tem tamanho 2^exp
    vetor = malloc(TAM * sizeof(int));      //Alocação de memória
    tids = malloc(TAM * sizeof(pthread_t)); //Alocação de memória
    preecher();
    pthread_cond_init(&cond_barreira, NULL);  //Inicialização da variável condicional
    pthread_mutex_init(&lock_barreira, NULL); //Inicialização da variável de exclusão mútua
    for (size_t i = 0; i < TAM; i++)          //Criação das threads
    {
        if (pthread_create(&tids[i], NULL, tarefa, (void *)i))
        {
            puts("ERRO -- CREATE");
            return 1;
        }
    }
    for (size_t i = 0; i < TAM; i++) //Garante que a main continue apenas quando todas as threads terminarem;
    {
        if (pthread_join(tids[i], NULL))
        {
            puts("ERRO -- JOIN");
            return 1;
        }
    }
    verifica();
    pthread_cond_destroy(&cond_barreira);  //Libera a variável condicional
    pthread_mutex_destroy(&lock_barreira); //Libera a variável de exclusão mútua
    return 0;
}
