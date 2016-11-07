package org.ff4j.test.audit;

import org.ff4j.inmemory.EventRepositoryInMemory;
import org.ff4j.store.EventRepository;

/**
 * Test for publisher and InMemory Event repository.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class InMemoryEventRepositoryTest extends AbstractEventRepositoryTest {
    
    /** {@inheritDoc} */
    @Override
    protected EventRepository initRepository() {
        return new EventRepositoryInMemory(60);
    }
    
}
