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
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class CharacterImporter {
    public static List<Personagem> importFromZip(String zipPath, int dropRow, int dropCol) 
            throws IOException, ClassNotFoundException {
        List<Personagem> characters = new ArrayList<>();
        Path tempDir = Files.createTempDirectory("game_import");
        
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path filePath = tempDir.resolve(entry.getName());
                if (!entry.isDirectory()) {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zipIn, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipIn.closeEntry();
            }
        }

        // Use try-with-resources for directory stream
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempDir, "*.ser")) {
            for (Path file : stream) {
                try (ObjectInputStream in = new ObjectInputStream(
                     new BufferedInputStream(new FileInputStream(file.toFile())))) {
                    
                    Personagem p = (Personagem) in.readObject();
                    p.setPosicao(dropRow, dropCol);
                    characters.add(p);
                }
            }
        }
        
        // Clean up in a separate try block
        try {
            Files.walk(tempDir)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        } catch (IOException e) {
            System.err.println("Error cleaning up temp files: " + e.getMessage());
        }
             
        return Collections.unmodifiableList(characters); // Return immutable list
    }
}