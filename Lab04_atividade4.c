#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>

int count = 1;//Estado do programa
pthread_cond_t condicional1, condicional2;
pthread_mutex_t lock;//Variável de exclusão mútua 
void *T1(void *arg) {
    pthread_mutex_lock(&lock);
    printf("Seja bem-vindo!\n");
    count++;//muda o estado
    pthread_mutex_unlock(&lock);
    pthread_cond_broadcast(&condicional1);
    pthread_exit(NULL);
}
void * T2(void* arg){
    pthread_mutex_lock(&lock);
    while (count==1)
    {
        pthread_cond_wait(&condicional1,&lock);
    }
    count++;
    printf("Fique a vontade.\n");
    pthread_mutex_unlock(&lock);
    pthread_cond_signal(&condicional2);
    pthread_exit(NULL);
}
void * T3(void* arg){
    pthread_mutex_lock(&lock);
    while (count==1)
    {
        pthread_cond_wait(&condicional1, &lock);
    }
    count++;
    printf("Sente-se por favor.\n");    
    pthread_mutex_unlock(&lock);
    pthread_cond_signal(&condicional2);
    pthread_exit(NULL);
}
void * T4(void* arg){
    pthread_mutex_lock(&lock);
    while (count!=4)
    {
        //bloqueia a thread se o programa ainda não chegou no estado certo
        pthread_cond_wait(&condicional2, &lock);
    }
    printf("Volte sempre!\n");
    pthread_mutex_unlock(&lock);
    pthread_exit(NULL);
}

int main(int argc, char const *argv[])
{
    pthread_mutex_init(&lock, NULL);//inicialização da variavel de exclusão mútua
    pthread_cond_init(&condicional1, NULL);//incialização da variável condicional
    pthread_cond_init(&condicional2, NULL); //incialização da variável condicional
    pthread_t tids[4];//identificadores das threads
    if (pthread_create(&tids[0],NULL,T1,NULL)||pthread_create(&tids[1],NULL,T2,NULL)||pthread_create(&tids[2],NULL,T3,NULL)||pthread_create(&tids[3],NULL,T4,NULL))
    {
        return 1;
    }
    pthread_exit(NULL);

    return 0;
}
