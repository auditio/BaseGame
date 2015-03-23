package org.auditio.game;

import android.util.Log;

import java.util.Random;

/**
 * Created by auditio on 15-03-01.
 */
public class Equation {
    Number [] equation;

    public Equation (Number[] nums){
        this.equation = new Number[nums.length * 2 - 1];

        generateEquation(nums);
    }

    /*
     * 0 = +
     * 1 = -
     */
    private Number generateOperation (){
        Random rand = new Random();

        return new Number(rand.nextInt(9) % 2) ;
    }

    private void generateEquation(Number[] nums){

        for (int i = 0, j = 0; i < this.equation.length; i++){
            if(i % 2 == 0)
                this.equation[i]= nums[j++];
            else{
                this.equation[i] = generateOperation();
            }
        }
    }

    public String getEquation(){
        String eq = "";

        for (int i = 0; i < this.equation.length; i++){
            if(i % 2 == 0){
                eq = eq + String.valueOf(this.equation[i].getNum());
            }else {
                if(this.equation[i].getNum() == 0)
                    eq = eq + "+";
                else
                    eq = eq + "-";

            }

        }

        //Log.d(TAG, eq + " = " + solution());
        return eq;
    }

    private int solution(){
        int answer = this.equation[0].getNum();

        for (int i = 1; i < this.equation.length; i++){
            if(this.equation[i].getNum() == 0)
                answer = answer + this.equation[++i].getNum();
            else
                answer = answer - this.equation[++i].getNum();
        }

        return answer;
    }

    public int getSolution(){
        return solution();
    }


}
