/* 
 * File:   oled_library.h
 * Author: ritwik
 *
 * Created on April 13, 2015, 6:06 PM
 */

#ifndef OLED_LIBRARY_H
#define	OLED_LIBRARY_H

#ifdef	__cplusplus
extern "C" {
#endif
    
    void write_char(char a,int row,int col);
    void write_string(char ch[],int row,int col);
    void oled_accels(short ax,short ay);

#ifdef	__cplusplus
}
#endif

#endif	/* OLED_LIBRARY_H */

