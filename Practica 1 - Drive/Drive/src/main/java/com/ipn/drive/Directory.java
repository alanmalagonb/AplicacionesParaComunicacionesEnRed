/* Práctica 1 Aplicación Drive para almacenamiento de archivos
   Alumnos: Malagon Baeza Alan Adrian
            Martinez Chavez Jorge Alexis
   6CM1 Aplicaciones para Comunicaciones en Red
*/

package com.ipn.drive;

import java.io.Serializable;

// Modelo Archivo/Carpeta
public class Directory implements Serializable {

    // Nombre del archivo/carpeta
    private String name;

    // Fuente de la imagen
    private String imgSrc;

    // Tamaño del archivo/carpeta
    private long size;

    // Getters & Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
