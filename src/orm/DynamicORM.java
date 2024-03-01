package orm;

import java.sql.Connection;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DynamicORM<T> extends DynamicConnection<T> {

    protected String defaultFileName = "dynamic-connection.txt";

    public String getFilePath() {
        File currentDir = new File(System.getProperty("user.dir")); // Le répertoire de travail actuel
        List<File> foundFiles = new ArrayList<>();
        searchFile(currentDir, defaultFileName, foundFiles); // Commencer la recherche
        if (!foundFiles.isEmpty()) {
            // Retourner le chemin du premier fichier trouvé
            return foundFiles.get(0).getAbsolutePath();
        } else {
            // Aucun fichier trouvé
            return null;
        }
    }

    // Méthode récursive pour rechercher le fichier dans le répertoire donné et ses
    // sous-répertoires
    private void searchFile(File directory, String fileName, List<File> foundFiles) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        searchFile(file, fileName, foundFiles); // Appel récursif pour les sous-répertoires
                    } else if (file.getName().equals(fileName)) {
                        foundFiles.add(file); // Ajouter le fichier trouvé à la liste
                    }
                }
            }
        }
    }

    @Override
    public Connection getConnection() {
        // mitady anle informations de connection anatinle projet

        throw new UnsupportedOperationException("Unimplemented method 'getConnection'");
    }

}
