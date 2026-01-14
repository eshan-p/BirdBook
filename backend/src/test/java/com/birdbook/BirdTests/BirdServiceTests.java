package com.birdbook.BirdTests;

import com.birdbook.models.Bird;
import com.birdbook.repository.BirdDAO;
import com.birdbook.service.BirdService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BirdServiceTest {

    @Mock
    private BirdDAO birdDAO;

    @InjectMocks
    private BirdService birdService;

    private Bird bird;
    private ObjectId birdId;

    @BeforeEach
    void setUp() {
        birdId = new ObjectId();
        bird = new Bird();
        bird.setId(birdId);
        bird.setCommonName("Cardinal");
        bird.setImageURL("image-url");
    }

    @Test
    void getBirdByCommonName_returnsBird() {
        when(birdDAO.findByCommonName("Cardinal")).thenReturn(bird);

        Bird result = birdService.getBirdByCommonName("Cardinal");

        assertNotNull(result);
        assertEquals("Cardinal", result.getCommonName());
        verify(birdDAO).findByCommonName("Cardinal");
    }

    @Test
    void getAllBirds_returnsList() {
        when(birdDAO.findAll()).thenReturn(List.of(bird));

        List<Bird> birds = birdService.getAllBirds();

        assertEquals(1, birds.size());
        verify(birdDAO).findAll();
    }

    @Test
    void addBird_insertsAndReturnsBird() {
        when(birdDAO.insert(bird)).thenReturn(bird);

        Bird result = birdService.addBird(bird);

        assertNotNull(result);
        verify(birdDAO).insert(bird);
    }

    @Test
    void deleteBird_whenBirdExists_deletesBird() {
        when(birdDAO.existsById(birdId)).thenReturn(true);

        birdService.deleteBird(birdId);

        verify(birdDAO).deleteById(birdId);
    }

    @Test
    void deleteBird_whenBirdDoesNotExist_throwsException() {
        when(birdDAO.existsById(birdId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> birdService.deleteBird(birdId)
        );

        assertEquals("Bird not found.", exception.getMessage());
        verify(birdDAO, never()).deleteById(any());
    }

    @Test
    void updateBird_whenBirdExists_updatesAndSaves() {
        Bird updateRequest = new Bird();
        updateRequest.setCommonName("Blue Jay");
        updateRequest.setImageURL("new-image-url");

        when(birdDAO.findById(birdId)).thenReturn(Optional.of(bird));
        when(birdDAO.save(any(Bird.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bird updatedBird = birdService.updateBird(birdId, updateRequest);

        assertEquals("Blue Jay", updatedBird.getCommonName());
        assertEquals("new-image-url", updatedBird.getImageURL());
        verify(birdDAO).save(bird);
    }

    @Test
    void updateBird_whenBirdDoesNotExist_throwsException() {
        when(birdDAO.findById(birdId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> birdService.updateBird(birdId, bird)
        );

        assertEquals("Bird not found.", exception.getMessage());
        verify(birdDAO, never()).save(any());
    }
}

