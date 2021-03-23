package com.smg.thewardrobe.utilities;

import java.util.ArrayList;
import java.util.Random;

public class GetRandomImageUtility {

    public static int getRandomImageFromArray(ArrayList arr){

        int max = arr.size()-1;
        int random = new Random().nextInt(max + 1);

        return random;
    }
}
