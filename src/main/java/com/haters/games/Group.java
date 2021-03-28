package com.haters.games;

import com.haters.games.physics.GroupColor;
import com.haters.games.physics.Star;
import com.haters.games.physics.Sequence;
import com.haters.games.physics.SpaceShip;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by flvs on 11/2/15.
 */

public class Group {

    private final List<SpaceShip> members = new ArrayList<SpaceShip>();
    private final List<Star> taken_stars = new ArrayList<Star>();
    private final int id;
    private final GroupColor groupColor;

    public Group(GroupColor color) {
        this.groupColor = color;
        id = Sequence.getSequence();
    }

    public void addMember(SpaceShip member) {
        members.add(member);
    }

    public void removeMember(SpaceShip member) {
        members.remove(member);
    }

    public void takeStar(Star star) {
        Group oldGroup = star.getGroup();

        if (star.getGroup() != null)
            star.getGroup().looseStar(star);
        taken_stars.add(star);
        star.setGroup(this);

        for (SpaceShip ship : star.getShipInRange()) {
            if (ship.getGroup() != null) {
                if (ship.getGroup().equals(oldGroup)) {
                    ship.powerDown();
                } else if (ship.getGroup().equals(this)) {
                    ship.powerUp();
                }
            }
        }
    }

    private void looseStar(Star star) {
        taken_stars.remove(star);
    }

    public List<SpaceShip> getMembers() {
        return members;
    }

    public List<Star> getStars() {
        return taken_stars;
    }

    public int getId() {
        return id;
    }

    public GroupColor getColor() {
        return groupColor;
    }

    public String getColorHex() {
        return String.format("0x%06x", groupColor.getColor().getRGB() & 0x00FFFFFF);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return id == ((Group) obj).getId();
    }

}
