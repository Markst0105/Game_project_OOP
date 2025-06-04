/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author marks
 */
import Modelo.Personagem;
import java.io.*;
import java.util.List;
import java.util.zip.*;

public class CharacterExporter {
    public static void exportCharacters(List<Personagem> characters, String zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            for (int i = 0; i < characters.size(); i++) {
                ZipEntry entry = new ZipEntry("character" + i + ".ser");
                zos.putNextEntry(entry);
                
                try (ObjectOutputStream oos = new ObjectOutputStream(zos)) {
                    oos.writeObject(characters.get(i));
                }
                
                zos.closeEntry();
            }
        }
    }
}