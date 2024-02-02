package io.github.enderor.utils.actions;

public interface Action<I, O> {
  O accept(I input);
}
