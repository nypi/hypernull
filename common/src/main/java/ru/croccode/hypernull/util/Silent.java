package ru.croccode.hypernull.util;

import java.util.function.Supplier;

public final class Silent {

	private Silent() {
	}

	public static Runnable runnableOf(Block block) {
		return () -> {
			try {
				block.invoke();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	public static <T> Supplier<T> supplierOf(BlockWithResult<T> block) {
		return () -> {
			try {
				return block.invoke();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		};
	}

	@FunctionalInterface
	public interface Block {

		void invoke() throws Exception;
	}

	@FunctionalInterface
	public interface BlockWithResult<T> {

		T invoke() throws Exception;
	}
}
