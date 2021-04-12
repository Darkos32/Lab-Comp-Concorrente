#include "stdio.h"
#include "stdlib.h"
#include "pthread.h"
#include "timer.h"
#include "math.h"
int  nthreads;//*Número de threads
long long int nelementos;//*Número de elementos da série


int potencia(int base,int expoente){
    //*Função simplicada que calcula uma base elevada a expoentes positivos
    int resultado = 1; //*Resultado da operação
    if (expoente<0)
    {
        printf("ERRO- Essa funcao lida apenas com expoentes positivos\n");
    }
    for (size_t i = 0; i < expoente; i++)
    {
        resultado *= base;
    }
    return resultado;
}

void* tarefa(void* arg){
    long int id = (long int )arg;
    int blocos = nelementos / nthreads;
    int inicio = id * blocos,fim;
    double* soma;
    soma = malloc(sizeof(double));
    *soma = 0;
    if ((id + 1) * blocos > nelementos)
    {
        fim = nelementos;
    }else{
        fim = (id + 1) * blocos;
    }
    for (size_t i = inicio; i < fim; i++)
    {
        *soma += potencia(-1, i) * (1 / ((float)(2 * i + 1)));
    }
    pthread_exit((void *)soma);
}

int main(int argc, char const *argv[])
{
    //!Declaração de varáveis
    double pi_sequencial=0, pi_concorrente=0,*temp; //*Aproximações de pi obtidas pelas versões do programa
    double inicio_sequencial, fim_sequencial,inicio_concorrente,fim_concorrente;//*Variáveis para controle de tempo

    //!Tratamento da entrada
    if (argc<3)
    {
        //*Verifica o número de argumentos passados
        printf("ERRO - Numero de argumentos\n");
        return -1;
    }
    nelementos = atoll(argv[1]);//*Converte de String para long long int
    nthreads = atoi(argv[2]);//*Converte de String para int

    //!Calculo sequencial
    GET_TIME(inicio_sequencial);
    for (size_t i = 0; i < nelementos; i++)
    {
        pi_sequencial += potencia(-1, i) * (1 / ((float)(2 * i + 1)));
    }
    pi_sequencial *= 4;
    GET_TIME(fim_sequencial);
    //!Calculo concorrente
    GET_TIME(inicio_concorrente);
    pthread_t *tids;                            //*Identificadores das threads
    tids = malloc(nthreads * sizeof(pthread_t));//*Alocação do vetor
    for (size_t i = 0; i < nthreads; i++)
    {
        //*Criação das threads
        if (pthread_create(&tids[i],NULL,tarefa,(void*)i))
        {
            printf("ERRO-Criacao de threads");
            return -1;
        }
    }
    for (size_t i = 0; i < nthreads; i++)
    {
        //*Pausa a main e recebe o retorno das threads
        if (pthread_join(tids[i],(void**)&temp))
        {
            printf("ERRO-JOIN");
        
        }
        pi_concorrente += *temp;
    }
    pi_concorrente *= 4;
    GET_TIME(fim_concorrente);
    //!Saída
    printf("Tempo sequencial: %.15lf segundos\n", fim_sequencial - inicio_sequencial);
    printf("Tempo concorrente: %.15lf segundos\n", fim_concorrente - inicio_concorrente);
    printf("Ganho de velocidade: %.15lf\n", (fim_sequencial - inicio_sequencial) / (fim_concorrente - inicio_concorrente));
    printf("Valor de pi usado como referencia %.15lf\n", M_PI);
    printf("Valor de pi calculado sequencialmente %.15lf\n", pi_sequencial);
    printf("Valor de pi calculado de forma concorrente %.15lf\n", pi_concorrente);
    //!Desalocação de memória
    free(tids);
    free(temp);
    return 0;
}
