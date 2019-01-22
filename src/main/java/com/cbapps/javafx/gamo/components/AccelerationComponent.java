package com.cbapps.javafx.gamo.components;

import com.cbapps.javafx.gamo.objects.GameVector;

public class AccelerationComponent implements GameObjectEditor {
	private double acceleration;
	private double maxSpeed;
	private double speed;
	private double targetValue;
	private double value;

	private TargetSupplier targetGetter;
	private ValueConsumer valueSetter;

	private AccelerationComponent() {}

	public static AccelerationComponent horizontalRotation(double acceleration, double maxSpeed) {
		AccelerationComponent editor = new AccelerationComponent();
		editor.acceleration = acceleration;
		editor.maxSpeed = maxSpeed;
		editor.targetGetter = (t -> t.getRotation().getHorizontal());
		editor.valueSetter = (c, t, v) -> c.withRotation(t.getRotation().withHorizontal(v));
		return editor;
	}

	@Override
	public GameVector onUpdate(double elapsedSeconds, GameVector current, GameVector target) {
		targetValue = targetGetter.getTargetValue(target);

		double stopDistance = speed * speed / (2 * acceleration);

		boolean forwards = targetValue > value;

		if (forwards) {
			if (stopDistance >= targetValue - value) {
				speed -= acceleration * elapsedSeconds;
				if (speed < 0)
					speed = 0;
			}
			else
				speed += acceleration * elapsedSeconds;
		} else {
			if (stopDistance >= value - targetValue) {
				speed += acceleration * elapsedSeconds;
				if (speed > 0)
					speed = 0;
			}
			else
				speed -= acceleration * elapsedSeconds;
		}

		value += speed * elapsedSeconds;

		// Cap speed.
		speed = speed > maxSpeed ? maxSpeed : speed;
		speed = speed < -maxSpeed ? -maxSpeed : speed;

		return valueSetter.setValue(current, target, value);
	}

	private interface TargetSupplier {
		double getTargetValue(GameVector targetVector);
	}

	private interface ValueConsumer {
		GameVector setValue(GameVector currentVector, GameVector targetVector, double value);
	}
}
