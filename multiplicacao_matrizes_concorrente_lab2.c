#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "timer.h"
float *matrizA, *matrizB, *saida;
int tam, nthreads;

void *tarefa(void *arg)
{
    //*Multiplicação das matrizes
    int comeco = ( long int)arg;
    for (size_t i = comeco; i < tam; i += nthreads)
    {
        for (size_t j = 0; j < tam; j++)
        {
            for (size_t k = 0; k < tam; k++)
            {
                saida[i * tam + j] += matrizA[i * tam + k] * matrizB[k * tam + j];
            }
        }
    }
    pthread_exit(NULL);
}
// void print_saida()
// {
//     for (size_t i = 0; i < tam; i++)
//     {
//         for (size_t j = 0; j < tam; j++)
//         {
//             printf("%.1f ", saida[i * tam + j]);
//         }
//         puts("");
//     }
// }
int verifica()
{
    //*Verifica se o resultado da multiplicação foi o esperado
    for (size_t i = 0; i < tam; i++)
    {
        for (size_t j = 0; j < tam; j++)
        {
            if (saida[i*tam+j] != tam)
            {
                puts("ERRO--Multiplicacao");
                return -1;
            }
        }
    }
    puts("Sucesso");
    return 0;
}
int main(int argc, char const *argv[])
{
    double inicio, fim, delta;//*Variáveis para medição do tempo de execução
    //*Tratamento da entrada
    if (argc < 3)
    {
        //*Verifica se o programa tem parâmetros suficientes para executar
        puts("ERRO--Argumentos insuficentes");
        return -1;
    }
    tam = atoi(argv[1]);//*Tamanho das matrizes quadradas
    nthreads = atoi(argv[2]);//*Número de threads 
    if (nthreads > tam)
    {
        //*Se o usuário pede mais threads que elementos na matriz,
        //*o programa cria apenas tantas threads quanto elementos na matriz.
        nthreads = tam;
    }

    //*Inicialização das estruturas
    GET_TIME(inicio);
    matrizB = (float *)malloc(tam * tam * sizeof(float));//*Alocação de espaço para a matriz
    matrizA = (float *)malloc(tam * tam * sizeof(float));//*Alocação de espaço para a matriz
    saida = (float *)malloc(tam * tam * sizeof(float));  //*Alocação de espaço para a matriz
    if (matrizA == NULL || matrizB == NULL || saida == NULL)
    {
        //*Verifica se houve erro de alocação em alguma das matrizes
        puts("ERRO--Memoria insifuciente");
    }
    for (size_t i = 0; i < tam; i++)
    {
        for (size_t j = 0; j < tam; j++)
        {
            matrizA[i * tam + j] = 1;//*Preenche toda a matriz com '1's
            matrizB[i * tam + j] = 1; //*Preenche toda a matriz com '1's
            saida[i * tam + j] = 0;//*Preenche toda a matriz com '0's
        }
    }
    GET_TIME(fim);
    delta = fim - inicio;
    printf("Tempo de inicializacao : %lf\n", delta);
    //*Criação de Threads
    GET_TIME(inicio);
    pthread_t *tids;//*Identificadores das threads
    tids = (pthread_t *)malloc(nthreads * sizeof(pthread_t));//*Alocação de epaço para o vetor
    for (long int i = 0; i < nthreads; i++)
    {
        //*Cria as threads
        if (pthread_create(&tids[i], NULL, tarefa, (void *)i)  )
        {
            puts("ERRO--Thread create");
            return -1;
        }
    }
    for (size_t i = 0; i < nthreads; i++)
    {
        //*Garante que o programa só terminará após todas as threads
        if (pthread_join(tids[i], NULL))
        {
            puts("ERRO--Thread join");
            return -1;
        }
        }
    
    GET_TIME(fim);
    delta = -inicio + fim;
    printf("Tempo de multiplicacao : %lf\n", delta);
    GET_TIME(inicio);
    verifica();
    free(matrizA);
    free(matrizB);
    free(saida);
    GET_TIME(fim);
    delta = -inicio + fim;
    printf("Tempo de verificacao : %lf\n", delta);

    return 0;
}
