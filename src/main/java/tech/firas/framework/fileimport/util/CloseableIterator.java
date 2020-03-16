package tech.firas.framework.fileimport.util;

import java.io.Closeable;
import java.util.Iterator;

public interface CloseableIterator<T> extends Closeable, Iterator<T> {
}
