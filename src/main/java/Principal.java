import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Principal {

    public static void main(String[] args) throws Exception {
        ArrayList<JSONObject> liste = new ArrayList<>();
        boolean estUneDateValide;
        boolean estUnClientValide;
        boolean estUnContratValide;
        boolean estUnMontantValide;
        String fichierEntree = args[0];
        String fichierSortie = args[1];
        String donnees = args[2];

        if (args.length < 2) {
            System.out.println("Arguments invalides");
            System.exit(1);
        }

        Reclamation reclamation = new Reclamation(fichierEntree, fichierSortie);
        reclamation.load();

        liste = reclamation.getReclamationList();
        estUnClientValide = reclamation.validerNumeroClient(reclamation.getClient());
        System.out.println("clientValide " + estUnClientValide);
        estUnContratValide = reclamation.validerContrat(reclamation.getContrat().charAt(0));
        System.out.println("contratValide " + estUnContratValide);
        estUneDateValide = reclamation.validateDate(liste);
        System.out.println("dateValide " + estUneDateValide);
        estUnMontantValide = reclamation.validerMontant(liste);
        System.out.println("montantValide " + estUnMontantValide);


        if (estUneDateValide & estUnClientValide & estUnContratValide & estUnMontantValide) {

            reclamation.showAll(liste);
            reclamation.save();

        } else {
            try (FileWriter f = new FileWriter(args[1])) {
                JSONObject newJsonObj = new JSONObject();
                newJsonObj.put("message", "Données invalides");

                f.write(newJsonObj.toString(1));
                f.flush();
                f.close();
            } catch (IOException e) {
                throw new ReclamationException(e.toString());
            }

        }

    }
}

