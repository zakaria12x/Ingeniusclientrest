package com.zakaria.ingeniusclientrest.pojo;

/**
 * Created by zakaria on 31/08/2016.
 */
public class Projet {


    private int idProjet;
    private String nomProjet;

    public Projet(int idProjet, String nomProjet) {
        super();
        this.idProjet = idProjet;
        this.nomProjet = nomProjet;
    }
    public Projet() {
        super();
    }
    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {

        this.idProjet = idProjet;
    }
    @Override
    public String toString() {
        return nomProjet ;
    }
}
