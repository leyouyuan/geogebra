package org.geogebra.web.full.gui.toolbar;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * @author csilla
 *
 */
public class ToolButton extends StandardButton {

	private int mode;
	private AppW appW;
	private String selectedColor;

	/**
	 * @param mode
	 *            tool mode
	 * @param app
	 *            see {@link AppW}
	 */
	public ToolButton(int mode, AppW app) {
		super(AppResources.INSTANCE.empty(), app.getLocalization()
				.getMenu(EuclidianConstants.getModeText(mode)), 24);
		this.mode = mode;
		this.appW = app;
		this.selectedColor = app.getGeoGebraElement().getPrimaryColor(app.getFrameElement());
		setStyleName("toolButton");
		setAccessible();
		setSelected(false); // update icon
	}

	private void setAccessible() {
		String altText = appW.getLocalization()
				.getMenu(EuclidianConstants.getModeText(mode)) + ". "
				+ appW.getToolHelp(mode);
		setAltText(altText);
		getElement().setAttribute("mode", mode + "");
		getElement().setId("mode" + mode);
	}

	/**
	 * @param selected
	 *            true if tool is selected -> use teal img
	 */
	public void setSelected(final boolean selected) {
		final int iconMode = mode;
		final AppW app = appW;
		GWT.runAsync(GGWToolBar.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// failed loading toolbar
			}

			@Override
			public void onSuccess() {
				setIcon(selected
						? GGWToolBar.getColoredImageForMode(
								ToolbarSvgResources.INSTANCE, iconMode, app,
								selectedColor)
						: GGWToolBar.getImageURLNotMacro(
								ToolbarSvgResources.INSTANCE, iconMode, app));
			}
		});
	}

	/**
	 * set localized label of buttons
	 */
	public void setLabel() {
		setLabel(appW.getLocalization().getMenu(
				EuclidianConstants.getModeText(mode)));
		setAltText(appW.getLocalization().getMenu(
				EuclidianConstants.getModeText(mode))
				+ ". " + appW.getToolHelp(mode));
	}

	/**
	 * @return associated mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param appMode current mode of the app
	 */
	public void updateSelected(int appMode) {
		boolean selected = (mode == appMode) ||	isAdditionalToolSelected();
		getElement().setAttribute("selected",
				String.valueOf(selected));
		setSelected(selected);
	}

	private boolean isAdditionalToolSelected() {
		Construction cons = appW.getKernel().getConstruction();
		return (mode == EuclidianConstants.MODE_RULER && cons.getRuler() != null)
				|| (mode == EuclidianConstants.MODE_PROTRACTOR && cons.getProtractor() != null);
	}
}
