package me.linusdev.discordbotapi.api.interfaces;

import me.linusdev.discordbotapi.api.interfaces.copyable.Copyable;
import me.linusdev.discordbotapi.api.interfaces.updatable.Updatable;

public interface CopyAndUpdatable<T> extends Copyable<T>, Updatable {
}
