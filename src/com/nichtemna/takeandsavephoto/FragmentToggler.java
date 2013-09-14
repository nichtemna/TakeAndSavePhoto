package com.nichtemna.takeandsavephoto;

import com.nichtemna.takeandsavephoto.PhotoActivity.TransactionType;

public interface FragmentToggler {
	/**
	 * Switch fragments
	 * 
	 * @param type
	 *            - type specifying for how long switch fragments
	 */
	void toggleFragments(TransactionType type);
}
