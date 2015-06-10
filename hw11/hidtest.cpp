#ifdef WIN32
#include <windows.h>
#endif
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "hidapi.h"


#define MAX_STR 255

int main(int argc, char* argv[])
{
	int res;
	unsigned char buf[65];
	char message [50];
	int row;
	wchar_t wstr[MAX_STR];
	hid_device *handle;
	int i=0;
	short ax[1000],ay[1000], az[1000];
	

	// Initialize the hidapi library
	res = hid_init();

	// Open the device using the VID, PID,
	// and optionally the Serial number.
	handle = hid_open(0x4d8, 0x3f, NULL);


	printf("Enter the Row number: \n");
        scanf("%d", &row);
	printf("Enter the String: \n");
	scanf("%s",message);
	for (i = 0; i < 50; i++) {
		buf[i+3] = message[i];
	}

	buf[0] = 0x0;
	buf[1] = 0x82;
	buf[2] = row;
        res = hid_write(handle, buf, 65);
	 
	printf("Now entering Accelerometer mode \n");
        buf[0] = 0x0;
	buf[1] = 0x85; //start counting
	buf[2] = row;
        res = hid_write(handle, buf, 65);
	i=0;
	// Request state (cmd 0x83). Start accelerometer mode.
	while (i<1000)
{
	buf[0] = 0x0;
	buf[1] = 0x83;
	res = hid_write(handle, buf, 65);
	// Read requested state
	res = hid_read(handle, buf, 65);
	if(buf[0]==1)
	  {
	    ax[i] = buf[1] << 8 | buf[2];
	    ay[i] = buf[3] << 8 | buf[4];
	    az[i] = buf[5] << 8 | buf[6];
	    printf("X: %d Y: %d Z: %d \n",ax[i],ay[i],az[i]);
	    i++;

	     }
}
	printf("Accelerometer read successfully.\n");

	 FILE *ofp;
	 ofp = fopen("acc_data_1.txt","w");
	 fprintf(ofp,"Raw MAF FIR\n");
	 for(i = 0; i < 1000; i++){
		fprintf(ofp,"%d %d %d\r\n",ax[i],ay[i],az[i]);
	 }
	fclose(ofp);

	// Finalize the hidapi library
	res = hid_exit();

	return 0;
}
