package com.haters.games;

import com.haters.games.physics.Star;
import com.haters.games.physics.Sequence;
import com.haters.games.physics.SpaceShip;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flvs on 11/2/15.
 */
public class Group {

    private List<SpaceShip> members = new ArrayList<SpaceShip>();
    private List<Star> taken_stars = new ArrayList<Star>();
    private int id;
    private Color color;

    public Group(Color color){
        this.color = color;
        id = Sequence.getSequence();
    }

    public void addMember(SpaceShip member){
        members.add(member);
    }

    public void removeMember(SpaceShip member){
        members.remove(member);
    }

    public void takeStar(Star star){
        if(star.getGroup() != null)
            star.getGroup().looseStar(star);
        taken_stars.add(star);
        star.setGroup(this);
    }

    private void looseStar(Star star){
        taken_stars.remove(star);
    }

    public List<SpaceShip> getMembers() {
        return members;
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public String getColorHex() {
        return String.format("0x%06x", color.getRGB() & 0x00FFFFFF);
    }


}
