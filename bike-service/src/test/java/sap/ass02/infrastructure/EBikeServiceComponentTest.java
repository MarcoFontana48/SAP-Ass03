package sap.ass02.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.application.BikeService;
import sap.ass02.domain.EBike;
import sap.ass02.domain.P2d;
import sap.ass02.domain.V2d;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ddd.Repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

//! COMPONENT tests
public class EBikeServiceComponentTest {
    private final EBike eBike1 = new EBike("1", EBike.EBikeState.AVAILABLE, new P2d(1.0, 2.0), new V2d(3.0, 4.0), 5.0, 11);
    private final EBike eBike2 = new EBike("2", EBike.EBikeState.IN_USE, new P2d(2.0, 3.0), new V2d(4.0, 5.0), 6.0, 12);
    private BikeService eBikeService;
    
    @BeforeEach
    public void setUp() {
        Repository repository = new RepositoryMock();
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
        assertEquals(null, this.eBikeService.getEBike("3"));
    }
    
    private class RepositoryMock implements Repository {
        @Override
        public void init() {
        }
        
        @Override
        public boolean insertEbike(EBikeDTO eBike) {
            return false;
        }
        
        @Override
        public Optional<EBikeDTO> getEbikeById(String ebikeId) {
            if (ebikeId.equals(EBikeServiceComponentTest.this.eBike1.getId())) {
                return Optional.of(EBikeServiceComponentTest.this.eBike1.toDTO());
            } else {
                return Optional.empty();
            }
        }
        
        @Override
        public Iterable<EBikeDTO> getAllEBikes() {
            return List.of(EBikeServiceComponentTest.this.eBike1.toDTO(), EBikeServiceComponentTest.this.eBike2.toDTO());
        }
        
        @Override
        public boolean updateEBike(EBikeDTO ebike) {
            return false;
        }
    }
}
