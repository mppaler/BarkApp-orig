package com.codeworm.barkapp.m_Model;

/**
 * Created by Mariah on 11/03/2018.
 */

public class Spacecraft {
    String name;
    Long ratio,total_slot;


    public Spacecraft() {
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRatio(){ return ratio;}
    public void setRatio(Long ratio){this.ratio = ratio;}

    public Long getTotalSlot(){ return total_slot;}
    public void setTotalSlot(Long total_slot){this.total_slot = total_slot;}

}
