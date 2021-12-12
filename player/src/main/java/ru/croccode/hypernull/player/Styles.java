package ru.croccode.hypernull.player;

import java.util.Arrays;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public final class Styles {

	private static final List<Color> BOT_COLORS = Arrays.asList(
			Color.rgb(246, 81, 113),
			Color.rgb(130, 237, 78),
			Color.rgb(82, 132, 211),
			Color.rgb(240, 7, 53),
			Color.rgb(78, 226, 7),
			Color.rgb(22, 84, 183));

	public static final Color CLEAR_COLOR = Color.rgb(33, 33, 33);
	public static final Color TEXT_COLOR = Color.rgb(200, 200, 200);
	public static final Color COIN_COLOR = Color.rgb(255, 180, 49);
	public static final Color COLLECTED_COIN_COLOR = Color.rgb(255, 207, 122);
	public static final Color BLOCK_COLOR = Color.rgb(78, 155, 143);

	public static final double PADDING_HORIZONTAL = 12;
	public static final double PADDING_VERTICAL = 12;
	public static final double CANVAS_PADDING = 12;

	public static final Font DEFAULT_FONT = Font.font("Arial", FontPosture.REGULAR, 12);
	public static final Font INFO_FONT = Font.font("Arial", FontPosture.REGULAR, 14);
	public static final Font WEBDINGS_FONT = Font.font("Webdings", FontPosture.REGULAR, 24);

	private Styles() {
	}

	public static Color botColor(int botId) {
		return BOT_COLORS.get(botId % BOT_COLORS.size());
	}

	public static BorderStroke lineStroke(Color color) {
		return new BorderStroke(
				color,
				BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY,
				BorderWidths.DEFAULT);
	}

	public static Border lineBorder(Color color) {
		return new Border(lineStroke(color));
	}

	public static BackgroundFill solidFill(Color color) {
		return new BackgroundFill(
				color,
				CornerRadii.EMPTY,
				Insets.EMPTY);
	}

	public static Background solidBackground(Color color) {
		return new Background(solidFill(color));
	}

	public static Insets defaultPadding() {
		return new Insets(
				PADDING_VERTICAL,
				PADDING_HORIZONTAL,
				PADDING_VERTICAL,
				PADDING_HORIZONTAL);
	}
}

