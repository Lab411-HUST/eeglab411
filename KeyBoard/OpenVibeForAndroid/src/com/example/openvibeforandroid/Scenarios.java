package com.example.openvibeforandroid;

import java.util.Random;
import java.util.Vector;

public class Scenarios {
	/* Random ra kich ban flash training */
	public static Vector<Integer> generateTrain(int index) {
		Vector<Integer> vec = new Vector<Integer>();
		Random random = new Random();
		int counter = 0;
		int oldIndex = -1;
		while (counter < 64) {
			int newIndex;
			do {
				// index 0 - 11
				newIndex = random.nextInt(20);
				if (newIndex > 11)
					newIndex = index;

			} while (newIndex == oldIndex);
			oldIndex = newIndex;

			if (newIndex == index || newIndex - 6 == index) {
				vec.add(newIndex);
				if (newIndex == index)
					vec.add(newIndex + 6);
				else
					vec.add(newIndex - 6);
			} else {
				vec.add(newIndex);
			}
			counter++;
		}

		return vec;
	}

	/* Random ra kich ban flash running */
	private Vector<Integer> generateRun() {
		Vector<Integer> vec = new Vector<Integer>();
		Random random = new Random();
		int counter = 0;
		int oldIndex = -1;

		int[] max = new int[12];
		for (int i = 0; i < 12; i++) {
			max[i] = 12;
		}

		while (counter < 50) {   // so luong counter cu la 144
			int newIndex;
			do {
				// index 0 - 11
				newIndex = random.nextInt(12);

			} while (newIndex == oldIndex && max[newIndex] == 0);
			max[newIndex]--;
			oldIndex = newIndex;
			vec.add(newIndex);
			counter++;
		}
		return vec;
	}

}
