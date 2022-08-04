import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A data structure to store an indexes Unique Words with every appearance in
 * File and Indexes those Unique Files with every appearance of that word via
 * position
 *
 * @author Michael Miranda
 * @author University of San Francisco
 * @version Fall 2020
 */
public class InvertedIndexMulti extends InvertedIndex {

	/** A logger specifically for this class. */
	private static final Logger log = LogManager.getLogger(InvertedIndexMulti.class);

	/** The lock used to protect concurrent access to the underlying set. */
	private final SimpleReadWriteLock lock;
	
	/** Basic constructor with no parameter */
	public InvertedIndexMulti() {
		super();
		this.lock = new SimpleReadWriteLock();
	}

	/* Contains Function */

	@Override
	public boolean containsWord(String word) {
		lock.readLock().lock();
		try {
			return super.containsWord(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean containsLocationCount(String location) {
		lock.readLock().lock();
		try {
			return super.containsLocationCount(location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean containsLocation(String word, String location) {
		lock.readLock().lock();
		try {
			return super.containsLocation(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean containsIndex(String word, String location, Integer index) {
		lock.readLock().lock();
		try {
			return super.containsIndex(word, location, index);
		} finally {
			lock.readLock().unlock();
		}

	}

	/* Size-like functions */

	@Override
	public int inWordLocations(String word) {
		lock.readLock().lock();
		try {
			return super.inWordLocations(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int inWordIndexes(String word) {
		lock.readLock().lock();
		try {
			return super.inWordIndexes(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int inWordinLocationIndexes(String word, String location) {
		lock.readLock().lock();
		try {
			return super.inWordinLocationIndexes(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int uniqueWords() {
		lock.readLock().lock();
		try {
			return super.uniqueWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	/* Getter-Like Functions */

	@Override
	public Set<String> getWordSet() {
		lock.readLock().lock();
		try {
			return super.getWordSet();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getLocationSet(String word) {
		lock.readLock().lock();
		try {
			return super.getLocationSet(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> getIndexSet(String word, String location) {
		lock.readLock().lock();
		try {
			return super.getIndexSet(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> keySetCount() {
		lock.readLock().lock();
		try {
			return super.keySetCount();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Integer getCount(String location) {
		lock.readLock().lock();
		try {
			return super.getCount(location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/* Data-Adding Functions */

	@Override
	public void addIndex(String word, String location, Integer index) {
		lock.writeLock().lock();
		try {
			super.addIndex(word, location, index);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addAll(InvertedIndex other) {

		log.trace("addAll.lock.writeLock.lock()");
		lock.writeLock().lock();
		try {
			super.addAll(other);
		} finally {
			lock.writeLock().unlock();
		}

	
	}

	/* JSon - String Function */

	@Override
	public void toJson(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.toJson(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void toCountJson(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.toCountJson(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	/* Search Functions */

	@Override
	public List<SearchItem> search(Set<String> queries, boolean exact) {
		lock.readLock().lock();
		try {
			return super.search(queries, exact);
		} finally {
			lock.readLock().unlock();
		}
	}
}
