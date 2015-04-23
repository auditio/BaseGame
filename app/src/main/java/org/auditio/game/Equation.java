package org.auditio.game;

//import android.util.Log;

import java.util.Random;

/**
 * Created by auditio on 15-03-01.
 *
 * This class generates equation strings and solution to the equation
 *
 */
public class Equation {
    Number [] equation;
    //private static final String TAG = Equation.class.getSimpleName();

    /*
     * Given an array of operands, the constructor initializes member equation and generates the equation
     *
     * @param nums An array of type Number which contains randomly generated numbers to be used as operands
     *
     */
    public Equation (Number[] nums){
        this.equation = new Number[nums.length * 2 - 1];

        generateEquation(nums);
    }

    /*
     * Using random number generator and simple mapping to encode which operation to use
      * IF 0 is generated, operation = addition {0 = +}
      * IF 1 is generated, operation = subtraction {1 = -}
     *
     * This class can be expanded as needed
     */
    private Number generateOperation (){
        Random rand = new Random();

        return new Number(rand.nextInt(9) % 2) ;
    }


    /*
     * Generates an encoded equation where the odd indexes are encoded operations
     * defined in the generateOperation() method
     *
     * @param nums Holds the operands to be used int eh equation
     */
    private void generateEquation(Number[] nums){

        for (int i = 0, j = 0; i < this.equation.length; i++){
            if(i % 2 == 0)
                this.equation[i]= nums[j++];
            else{
                this.equation[i] = generateOperation();
            }
        }
    }

    /*
     * Converts the encoded equation to a string
     *
     * @return A string containing the decoded equation statement
     */
    public String getEquation(){
        String eq = "";

        // Based on the index of the Number object, it can be interpreted
        // as either an Operand or an Operation to generate the equation
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

    /* This method solves the equation generated and returns it as an integer
     *
     * @return An integer value that is the solution the equation generated
     */
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

    /*
     * @return An integer value that is the solution the equation generated
     */
    public int getSolution(){
        return solution();
    }


}
