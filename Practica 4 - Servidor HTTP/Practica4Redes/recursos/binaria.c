// Recursive Binary Search
#include <stdio.h>
#include <time.h>

//int num[10000000];
int num[1000000];
//int num[100000];
//int num[10000]; 
//int num[1000];
//int num[100];

// A recursive binary search function. It returns
// location of x in given array otherwise -1
int binarySearch(int arr[], int l, int r, int x)
{
    if (r >= l) {
        int mid = l + (r - l) / 2;
  
        // If the element is present at the middle
        // itself
        if (arr[mid] == x)
            return mid;
  
        // If element is smaller than mid, then
        // it can only be present in left subarray
        if (arr[mid] > x)
            return binarySearch(arr, l, mid - 1, x);
  
        // Else the element can only be present
        // in right subarray
        return binarySearch(arr, mid + 1, r, x);
    }
  
    // We reach here when element is not
    // present in array
    return -1;
}

void readFile(char filename[]){
    int i = 0, number;
    char str[10];
    FILE *fd;

    fd = fopen(filename,"rb");
    if (fd==NULL){ printf("Error opening file");}
    
    while (feof(fd)==0){
        
        fgets(str,10,fd);
        sscanf(str,"%d",&number);
        num[i] = number;
        i++;
    }
    fclose(fd);

}
  
int main(void){
    char filename[] = "numeros orden/un_millon.txt";
    clock_t start_t, end_t;
    double total_t;
    
    readFile(filename);
    int n = sizeof(num) / sizeof(num[0]);
    int x = 8908790; //Number to search
    
    start_t = clock();
    printf("Starting the binary search, start_t = %ld\n", start_t);
    int result = binarySearch(num, 0, n - 1, x);
    end_t = clock();
    printf("End of the binary search, end_t = %ld\n", end_t);

    (result == -1)
        ? printf("Element is not present in array")
        : printf("Element is present at index: %d", result);
    

    total_t = (double)(end_t - start_t) / CLOCKS_PER_SEC;
    printf("\nTotal time taken by CPU in miliseconds: %.3f\n", total_t*1000);
    printf("Exiting of the program...\n");

    return 0;
}