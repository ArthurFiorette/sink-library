package com.github.arthurfiorette.sinklibrary.data.storage;

import com.github.arthurfiorette.sinklibrary.data.database.Database;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @param <K> Storage key type
 * @param <V> Storage value type
 * @param <R> Database raw type
 *
 * @author https://github.com/arthurfiorette/sink-library/
 */
public interface Storage<K, V, R> {
  CompletableFuture<Void> save(K key, V value);

  default void saveSync(final K key, final V value) {
    this.save(key, value).join();
  }

  CompletableFuture<V> get(K key);

  default V getSync(final K key) {
    return this.get(key).join();
  }

  CompletableFuture<Collection<V>> getMany(Set<K> keys);

  default Collection<V> getManySync(final Set<K> keys) {
    return this.getMany(keys).join();
  }

  CompletableFuture<Collection<V>> operation(Function<Database<K, R>, Collection<R>> func);

  default Collection<V> operationSync(final Function<Database<K, R>, Collection<R>> func) {
    return this.operation(func).join();
  }

  CompletableFuture<V> operate(Function<Database<K, R>, R> func);

  default V operateSync(final Function<Database<K, R>, R> func) {
    return this.operate(func).join();
  }

  R serialize(V object);

  V deserialize(R raw);
}
