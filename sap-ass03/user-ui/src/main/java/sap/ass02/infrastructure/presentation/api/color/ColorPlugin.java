package sap.ass02.infrastructure.presentation.api.color;

import sap.ass02.infrastructure.presentation.api.Plugin;
import sap.ass02.infrastructure.presentation.view.VisualiserPanel;

public interface ColorPlugin extends Plugin {
	
	@Override
	default void apply(Object target) {
		if (target instanceof VisualiserPanel) {
            this.applyColor((VisualiserPanel) target);
		}
	}
	
	void applyColor(VisualiserPanel visualiserPanel);
}
