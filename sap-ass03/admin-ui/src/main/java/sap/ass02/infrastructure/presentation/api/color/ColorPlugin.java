package sap.ass02.infrastructure.presentation.api.color;

import sap.ass02.infrastructure.presentation.api.Plugin;
import sap.ass02.infrastructure.presentation.view.VisualiserPanel;

/**
 * Interface for color plugins
 */
public interface ColorPlugin extends Plugin {
	
	/**
	 * Apply the color to the target object
	 * @param target the target object
	 */
	@Override
	default void apply(Object target) {
		if (target instanceof VisualiserPanel) {
            this.applyColor((VisualiserPanel) target);
		}
	}
	
	/**
	 * Apply the color to the visualiser panel
	 * @param visualiserPanel the visualiser panel
	 */
	void applyColor(VisualiserPanel visualiserPanel);
}
