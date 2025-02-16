package sap.ass02.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.AbstractBike;
import sap.ass02.domain.application.BikeService;
import sap.ass02.domain.EBike;
import sap.ass02.domain.P2d;
import sap.ass02.domain.V2d;
import sap.ddd.Repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

//! INTEGRATION tests
public class EBikeServiceComponentTest {
    private final EBike eBike1 = new EBike("1", AbstractBike.BikeState.AVAILABLE, new P2d(1.0, 2.0), new V2d(3.0, 4.0), 5.0, 11);
    private final EBike eBike2 = new EBike("2", AbstractBike.BikeState.IN_USE, new P2d(2.0, 3.0), new V2d(4.0, 5.0), 6.0, 12);
    private BikeService eBikeService;
    
    @BeforeEach
    public void setUp() {
        Repository repository = mock(Repository.class);
        when(repository.getEbikeById("1")).thenReturn(java.util.Optional.of(this.eBike1.toDTO()));
        when(repository.getEbikeById("2")).thenReturn(java.util.Optional.of(this.eBike2.toDTO()));
        this.eBikeService = new BikeService();
        this.eBikeService.attachRepository(repository);
    }
    
    @Test
    public void getEBikeById() {
        this.eBikeService.getEBike("1");
        assertEquals(this.eBike1.getId(), this.eBikeService.getEBike("1").getId());
    }
    
    @Test
    public void getEBikeByIdNotFound() {
        this.eBikeService.getEBikes();
        assertNull(this.eBikeService.getEBike("3"));
    }
}
