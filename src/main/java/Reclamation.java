import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.IOUtils;

import java.util.*;
import java.lang.*;
import java.io.*;


public class Reclamation {
    private String client;
    private String contrat;
    private String mois;
    private String soin;
    private String date;
    private String montant;
    private String fichier;
    private String fichierSortie;
    private JSONObject jsonObj;

    public Reclamation() {
    }

    ;


    public Reclamation(String fichier, String fichierSortie) {

        this.fichier = fichier;
        this.fichierSortie = fichierSortie;
        this.jsonObj = null;
    }

    public Reclamation(String client, String contrat, String mois, String soin, String date, String montant) {
        this.client = client;
        this.contrat = contrat;
        this.mois = mois;
        this.soin = soin;
        this.date = date;
        this.montant = montant;

    }

    public Reclamation(String soin, String date, String montant) {
        this.soin = soin;
        this.date = date;
        this.montant = montant;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getContrat() {
        return contrat;
    }

    public void setContrat(String contrat) {
        this.contrat = contrat;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public String getSoin() {
        return soin;
    }

    public void setSoin(String soin) {
        this.soin = soin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }


    public boolean load() throws JSONException, ReclamationException {
        boolean result;
        try {
            String stringJson = IOUtils.toString(new FileInputStream(this.fichier), "UTF-8");
            JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(stringJson);
            this.jsonObj = jsonObj;
            String client = (String) jsonObj.get("client");
            setClient(client);
            String contrat = (String) jsonObj.get("contrat");
            setContrat(contrat);
            String date = (String) jsonObj.get("mois");
            setMois(date);
            System.out.println(getMois());
            result = true;
        } catch (FileNotFoundException e) {
            throw new ReclamationException("Le fichier d'entré n'exixte pas");
        } catch (JSONException e) {
            throw new ReclamationException("Le fichier json n'est pas valide");
        } catch (IOException e) {
            throw new ReclamationException(e.toString());
        }
        return result;
    }

    private JSONObject createJsonObj(ArrayList<JSONObject> liste) {

        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("reclamations", liste);

        return newJsonObj;
    }


    public void showAll(ArrayList<JSONObject> liste) {
        liste.forEach((elem) -> {
            System.out.println(elem.toString());
        });
    }

    public ArrayList<JSONObject> getReclamationList() {

        ArrayList<JSONObject> liste = new ArrayList<>();

        JSONArray collection = (JSONArray) JSONSerializer.toJSON(jsonObj.getString("reclamations"));
        for (int i = 0; i < collection.size(); i++) {
            setSoin(collection.getJSONObject(i).getString("soin"));
            setDate(collection.getJSONObject(i).getString("date"));
            setMontant(collection.getJSONObject(i).getString("montant"));
            liste.add(collection.getJSONObject(i));
        }


        return liste;

    }

    public boolean validateDate(ArrayList<JSONObject> liste) {
        boolean estUneDateValide = true;

        for (int i = 0; i < liste.size(); i++) {

            if (!liste.get(i).getString("date").substring(5, 7).trim().equals(getMois().substring(5, 7).trim())) {
                estUneDateValide = false;
            }
        }

        return estUneDateValide;
    }

    /*public boolean validerDate(String date) {
        String annee = date.substring(0, 4);
        String mois = date.substring(5,7);

       return Integer.parseInt(annee) <= Integer.parseInt(getMois().substring(0,4)) &&
               Integer.parseInt(mois) <= Integer.parseInt(getMois().substring(5,7));
    }*/

    public boolean validerNumeroClient(String str) {
        boolean estNumeroValide = false;
        if (str.matches("[0-9]+") && str.length() == 6) {
            estNumeroValide = true;
        }


        return estNumeroValide;
    }

    /*Le contrat doit être une des quatre lettres suivantes : A, B, C, D. La lettre doit toujours être en
    majuscule.*/

    public boolean validerContrat(char car) {
        boolean estUnContratValide = false;
        char caractere = Character.toUpperCase(car);
        if (caractere == 'A' || caractere == 'B' || caractere == 'C' || caractere == 'D') {
            estUnContratValide = true;
        }
        return estUnContratValide;
    }

    public boolean validerMontant(ArrayList<JSONObject> liste) {
        boolean estMontantValide = true;
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getString("montant").lastIndexOf("$")
                    != liste.get(i).getString("montant").length() - 1) {
                estMontantValide = false;
            }

        }
        return estMontantValide;
    }


    public boolean save() throws ReclamationException {
        boolean res;
        try (FileWriter f = new FileWriter(fichierSortie)) {
            JSONObject newJsonObj = new JSONObject();
            newJsonObj.put("client", jsonObj.getString("client"));
            newJsonObj.put("mois", jsonObj.getString("mois"));
            newJsonObj.put("remboursements", JSONArray.fromObject(getReclamationList()));
            f.write(newJsonObj.toString(1));
            f.flush();
            f.close();
            res = true;
        } catch (IOException e) {
            throw new ReclamationException(e.toString());
        }
        return res;
    }

}
