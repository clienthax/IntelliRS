package rs2.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayUtils {

	/**
	 * Removes an index from the given array.
	 * @param original
	 * @param toRemove
	 * @return
	 */
	public static int[] remove(int[] original, int toRemove) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		int[] removed = new int[original.length];
		for (int integer : original) {
			list.add(integer);
		}
		list.remove(toRemove);
		removed = listToArray(list);
		return removed;
	}

	/**
	 * Rearranges an array to fit the specified order movement.
	 * @param original The original array.
	 * @param selectedIndex The selected index to move.
	 * @param toSlot The slot to move the selected index to.
	 * @return
	 */
	public static int[] setZOrder(int[] original, int selectedIndex, int toSlot) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		int[] moved = new int[original.length];
		for (int integer : original) {
			list.add(integer);
		}
		list.remove(selectedIndex);
		list.add(toSlot, original[selectedIndex]);
		moved = listToArray(list);
		return moved;
	}

	/**
	 * Converts a list to an integer array.
	 * @param integers
	 * @return
	 */
	private static int[] listToArray(List<Integer> integers) {
	    int[] ints = new int[integers.size()];
	    int i = 0;
	    for (Integer n : integers) {
	        ints[i++] = n;
	    }
	    return ints;
	}

	/**
	 * Reverses the order of values in a given int array.
	 * @param data
	 */
	public static int[] reverse(int[] data) {
	    int left = 0;
	    int right = data.length - 1;
	    while(left < right) {
	        int temp = data[left];
	        data[left] = data[right];
	        data[right] = temp;
	        left++;
	        right--;
	    }
	    return data;
	}

	/**
	 * Returns whether or not an array contains duplicates.
	 * @param list
	 * @return
	 */
	public static boolean containsDuplicates(final int[] list) {
		Set<Integer> lump = new HashSet<Integer>();
		for (int i : list) {
			if (lump.contains(i)) {
				return true;
			}
			lump.add(i);
		}
		return false;
	}

}
