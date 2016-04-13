package mog.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liuminhang on 16/4/12.
 */
/*
基本的slot对象,即为基本的船舶结构.放有MOContainer,PositionSet等对象
 */
public class MOSlot {
    private MOContainer moContainer;
    private Set<MOSlotPosition> moSlotPositionSet;
    private int moveOrderSeq;


    MOSlot(){
        moSlotPositionSet = new HashSet<>();
    }

    public MOContainer getMoContainer() {
        return moContainer;
    }

    public void setMoContainer(MOContainer moContainer) {
        this.moContainer = moContainer;
    }

    public Set<MOSlotPosition> getMoSlotPositionSet() {
        return moSlotPositionSet;
    }

    public void setMoSlotPositionSet(Set<MOSlotPosition> moSlotPositionSet) {
        this.moSlotPositionSet = moSlotPositionSet;
    }

    public int getMoveOrderSeq() {
        return moveOrderSeq;
    }

    public void setMoveOrderSeq(int moveOrderSeq) {
        this.moveOrderSeq = moveOrderSeq;
    }
}
