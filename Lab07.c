#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>
sem_t semaforo1, semaforo2; //semáforos
int count = 0;              //contador
void *T1(void *arg)
{
    sem_wait(&semaforo2);
    printf("Volte sempre!\n");
}
void *T2(void *arg)
{
    sem_wait(&semaforo1);
    count++;
    printf("Fique a vontade.\n");
    sem_post(&semaforo1);
    if (count == 2) //verifica se é a terceira thread a executar
        sem_post(&semaforo2);
}
void *T3(void *arg)
{
    sem_wait(&semaforo1);
    count++;
    printf("Sente-se por favor.\n");
    sem_post(&semaforo1);
    if (count == 2) //verifica se é a terceira thread a executar
        sem_post(&semaforo2);
}
void *T4(void *arg)
{
    printf("Seja bem-vindo!\n");
    sem_post(&semaforo1);
}
int main(int argc, char const *argv[])
{
    sem_init(&semaforo1, 0, 0); //inicialização do semáforo
    sem_init(&semaforo2, 0, 0); //inicialização do semáforo
    pthread_t tids[4];          //identificadores das threads
    //inicialização das threads
    pthread_create(&tids[0], NULL, T1, NULL);
    pthread_create(&tids[1], NULL, T2, NULL);
    pthread_create(&tids[2], NULL, T3, NULL);
    pthread_create(&tids[3], NULL, T4, NULL);
    pthread_exit(NULL);      //impede que o programa termine sem o resto das threads terminarem
    sem_destroy(&semaforo1); //desalocação do semáforo
    sem_destroy(&semaforo2); //desalocação do semáforo
    return 0;
}
