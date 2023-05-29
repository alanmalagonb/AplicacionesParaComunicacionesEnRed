/* Práctica 1 Aplicación Drive para almacenamiento de archivos
   Alumnos: Malagon Baeza Alan Adrian
            Martinez Chavez Jorge Alexis
   6CM1 Aplicaciones para Comunicaciones en Red
*/

package com.ipn.drive;

// Listeners de clicks sobre listado de carpetas/archivos
public interface MyListener {
    public void onClickListener(Directory directory);

    public void onDoubleClickListener(Directory directory);
}
