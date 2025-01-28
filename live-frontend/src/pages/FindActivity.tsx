import React, { useState, useEffect, useCallback } from 'react';
import {
  GoogleMap,
  LoadScript,
  Marker
} from '@react-google-maps/api';
import { useNavigate } from 'react-router-dom';
import { Button } from '@mui/material';
import client from '../api/client';

interface Activity {
  id: string;
  name: string;
  category: string;
  distance: number;
  rating: number;
  lat: number;
  lng: number;
}

// Regular mock data (keeping higher rated/closer activities)
const mockActivities: Activity[] = [
  {
    id: '5',
    name: 'The Tipsy Crow',
    category: 'Bar',
    distance: 0.3,
    rating: 4.5,
    lat: 40.7234,
    lng: -73.9870
  },
  {
    id: '6',
    name: 'OMNIA Nightclub',
    category: 'Club',
    distance: 0.5,
    rating: 4.8,
    lat: 40.7589,
    lng: -73.9851
  },
  {
    id: '8',
    name: 'Central Park Runners',
    category: 'Run Club',
    distance: 0.8,
    rating: 4.7,
    lat: 40.7829,
    lng: -73.9654
  },
  {
    id: '9',
    name: 'Yankees Game',
    category: 'Sports Event',
    distance: 0.4,
    rating: 4.9,
    lat: 40.8296,
    lng: -73.9262
  },
  {
    id: '11',
    name: 'Natural History Museum',
    category: 'Museum',
    distance: 1.8,
    rating: 4.9,
    lat: 40.7813,
    lng: -73.9740
  },
  {
    id: '12',
    name: 'Rooftop Club',
    category: 'Club',
    distance: 0.6,
    rating: 4.4,
    lat: 40.7484,
    lng: -73.9857
  },
  {
    id: '15',
    name: 'Science Museum',
    category: 'Museum',
    distance: 1.7,
    rating: 4.7,
    lat: 40.7587,
    lng: -73.9787
  }
];

// New trending activities (keeping most popular/highly rated)
const mockTrendingActivities: Activity[] = [
  {
    id: '4',
    name: 'Runners Meetup',
    category: 'Run Club',
    distance: 1,
    rating: 4.9,
    lat: 40.7580,
    lng: -73.9855
  },
  {
    id: '7',
    name: 'Modern Art Gallery',
    category: 'Museum',
    distance: 1.2,
    rating: 4.6,
    lat: 40.7829,
    lng: -73.9654
  },
  {
    id: '9',
    name: 'Yankees Game',
    category: 'Sports Event',
    distance: 0.4,
    rating: 4.9,
    lat: 40.8296,
    lng: -73.9262
  },
  {
    id: '10',
    name: 'Speakeasy Bar',
    category: 'Bar',
    distance: 2.1,
    rating: 4.7,
    lat: 40.7505,
    lng: -73.9934
  },
  {
    id: '11',
    name: 'Natural History Museum',
    category: 'Museum',
    distance: 1.8,
    rating: 4.9,
    lat: 40.7813,
    lng: -73.9740
  },
  {
    id: '6',
    name: 'OMNIA Nightclub',
    category: 'Club',
    distance: 0.5,
    rating: 4.8,
    lat: 40.7589,
    lng: -73.9851
  }
];

const containerStyle = {
  width: '100%',
  height: '100%',
};

const defaultCenter = {
  lat: 40.7128,
  lng: -74.0060,
};

type PlaceResult = google.maps.places.PlaceResult;

const FindActivitiesPage: React.FC = () => {
  const navigate = useNavigate();

  // ---------- STATES ----------
  const [searchTerm, setSearchTerm] = useState('');
  const [showFilters, setShowFilters] = useState(false);
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [maxDistance, setMaxDistance] = useState<number>(10);
  const [minRating, setMinRating] = useState<number>(0);
  const [selectedDate, setSelectedDate] = useState<string>('');

  // Keep both states
  const [filteredActivities, setFilteredActivities] = useState<Activity[]>(mockActivities);
  const [trendingActivities, setTrendingActivities] = useState<Activity[]>(mockTrendingActivities);

  // Google Map
  const [mapInstance, setMapInstance] = useState<google.maps.Map | null>(null);
  const [places, setPlaces] = useState<PlaceResult[]>([]);

  // Selected activity & recommendation
  const [selectedActivity, setSelectedActivity] = useState<Activity | null>(null);
  const [recommendation, setRecommendation] = useState<string>('');
  const [showRecommendation, setShowRecommendation] = useState(false);

  // ---------- MAP / PLACES ----------
  const handleMapLoad = (map: google.maps.Map) => {
    setMapInstance(map);
  };

  const handleSearch = useCallback(() => {
    if (!mapInstance || !searchTerm) return;

    const service = new google.maps.places.PlacesService(mapInstance);
    const request: google.maps.places.TextSearchRequest = {
      query: searchTerm,
      location: defaultCenter,
      radius: maxDistance * 1609.34, // miles to meters
    };

    service.textSearch(request, (results, status) => {
      if (status === google.maps.places.PlacesServiceStatus.OK && results) {
        // Sort results by popularity
        const sortedResults = results.sort((a, b) => {
          const ratingA = a.rating || 0;
          const ratingB = b.rating || 0;
          const ratingsA = a.user_ratings_total || 0;
          const ratingsB = b.user_ratings_total || 0;
          // Weighted sort combining average rating + # of reviews
          return (ratingB * Math.log(ratingsB + 1)) - (ratingA * Math.log(ratingsA + 1));
        });

        setPlaces(sortedResults);

        // Convert to Activity interface
        const newActivities: Activity[] = sortedResults.map((place, index) => ({
          id: place.place_id || String(index),
          name: place.name || 'Unnamed Location',
          category: place.types?.[0] || 'Unknown',
          distance: 0,
          rating: place.rating || 0,
          lat: place.geometry?.location?.lat() || 0,
          lng: place.geometry?.location?.lng() || 0,
        }));

        setFilteredActivities(newActivities);

        // Fit bounds to show all results
        if (results.length > 0) {
          const bounds = new google.maps.LatLngBounds();
          results.forEach((p) => {
            if (p.geometry?.location) {
              bounds.extend(p.geometry.location);
            }
          });
          mapInstance.fitBounds(bounds);
        }
      }
    });
  }, [mapInstance, searchTerm, maxDistance]);

  // Trigger search on Enter
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  // ---------- FILTERS ----------
  const handleCategoryChange = (category: string) => {
    if (selectedCategories.includes(category)) {
      setSelectedCategories((prev) => prev.filter((c) => c !== category));
    } else {
      setSelectedCategories((prev) => [...prev, category]);
    }
  };

  // ---------- SELECTION / RECOMMENDATION ----------
  const handleSelectActivity = async (activity: Activity) => {
    setSelectedActivity(activity);
    try {
      const response = await client.post('/recommendations', {
        chosenActivity: activity.name,
      });
      setRecommendation(response.data.recommendation);
      setShowRecommendation(true);
    } catch (error) {
      console.error('Error getting recommendation:', error);
      setRecommendation('Sorry, no recommendations available at this time.');
      setShowRecommendation(true);
    }
  };

  // ---------- DONE BUTTON ----------
  const handleDone = () => {
    if (!selectedActivity) return;
    const savedFormData = JSON.parse(sessionStorage.getItem('scheduleFormData') || '{}');
    const updatedFormData = {
      ...savedFormData,
      chosenActivity: selectedActivity,
      followUpSuggestion: recommendation,
    };
    sessionStorage.setItem('scheduleFormData', JSON.stringify(updatedFormData));
    navigate('/schedules/new');
  };
  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
      {/* Top Section: Search and Filters */}
      <div style={{ padding: '1rem', borderBottom: '1px solid #eee' }}>
        {/* Search Bar */}
        <div style={{ 
          display: 'flex',
          gap: '0.5rem',
          alignItems: 'center'
        }}>
          <input
            type="text"
            placeholder="Search for bars, clubs, museums..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={handleKeyDown}
            style={{ 
              width: '400px',
              padding: '0.5rem',
            }}
          />
          <button 
            onClick={() => setShowFilters(!showFilters)}
            style={{ 
              padding: '0.3rem 1rem',
              fontSize: '0.9rem',
              cursor: 'pointer',
              backgroundColor: '#f5f5f5',
              border: '1px solid #ddd',
              borderRadius: '4px',
              whiteSpace: 'nowrap'
            }}
          >
            {showFilters ? 'Hide Filters' : 'Show Filters'}
          </button>
        </div>

        {/* Filters */}
        {showFilters && (
          <div style={{ marginTop: '1rem', display: 'flex', gap: '2rem' }}>
            <div>
              <h3>Categories</h3>
              <div>
                {['Bar', 'Club', 'Museum', 'Run Club', 'Sports Event'].map((cat) => (
                  <label key={cat} style={{ display: 'block', margin: '4px 0' }}>
                    <input
                      type="checkbox"
                      checked={selectedCategories.includes(cat)}
                      onChange={() => handleCategoryChange(cat)}
                    />
                    <span style={{ marginLeft: '6px' }}>{cat}</span>
                  </label>
                ))}
              </div>
            </div>

            <div>
              <h3>Distance (miles)</h3>
              <input
                type="range"
                min={1}
                max={20}
                value={maxDistance}
                onChange={(e) => setMaxDistance(Number(e.target.value))}
              />
              <span style={{ marginLeft: '8px' }}>{maxDistance} mi</span>
            </div>

            <div>
              <h3>Minimum Rating</h3>
              <input
                type="range"
                min={0}
                max={5}
                step={0.5}
                value={minRating}
                onChange={(e) => setMinRating(Number(e.target.value))}
              />
              <span style={{ marginLeft: '8px' }}>{minRating}+</span>
            </div>

            <div>
              <h3>Date (Optional)</h3>
              <input
                type="date"
                value={selectedDate}
                onChange={(e) => setSelectedDate(e.target.value)}
              />
            </div>
          </div>
        )}
      </div>

      {/* Middle Section: Map */}
      <div style={{ height: '50vh', position: 'relative' }}>
        <LoadScript
          googleMapsApiKey={import.meta.env.VITE_GOOGLE_MAPS_API_KEY || ''}
          libraries={['places']}
        >
          <GoogleMap
            onLoad={handleMapLoad}
            mapContainerStyle={{ width: '100%', height: '100%' }}
            center={defaultCenter}
            zoom={12}
          >
            {places.map((place) => {
              if (!place.geometry?.location) return null;
              return (
                <Marker
                  key={place.place_id}
                  position={{
                    lat: place.geometry.location.lat(),
                    lng: place.geometry.location.lng(),
                  }}
                  title={place.name}
                />
              );
            })}
          </GoogleMap>
        </LoadScript>

        {/* Recommendation popup */}
        {showRecommendation && (
          <div
            style={{
              position: 'absolute',
              top: '20px',
              left: '20px',
              backgroundColor: 'white',
              padding: '10px',
              border: '1px solid #ccc',
              borderRadius: '4px',
              width: '300px',
            }}
          >
            <h3>Follow-Up Activity Recommendation</h3>
            <p>{recommendation}</p>
            <Button variant="contained" onClick={handleDone}>
              Done
            </Button>
          </div>
        )}
      </div>

      {/* Bottom Section: Trending Activities (Horizontal Row) */}
      <div style={{ padding: '1rem', flex: 1 }}>
        <h2>Trending Activities 🔥</h2>

        {/* 
          1) display: 'flex' 
          2) whiteSpace: 'nowrap' keeps all cards on one line 
          3) overflowX: 'auto' => horizontal scrollbar if needed
        */}
        <div 
          style={{ 
            display: 'flex', 
            overflowX: 'auto', 
            whiteSpace: 'nowrap',
            gap: '1rem',
            padding: '0.5rem',
          }}
        >
          {trendingActivities
            .filter((act) => {
              if (selectedCategories.length > 0 && !selectedCategories.includes(act.category)) {
                return false;
              }
              if (act.rating < minRating) {
                return false;
              }
              return true;
            })
            .map((activity) => (
              <div
                key={activity.id}
                style={{
                  display: 'inline-block', // important for whiteSpace: nowrap
                  border: '1px solid #ccc',
                  borderRadius: '4px',
                  padding: '0.5rem',
                  minWidth: '220px', // ensure a reasonable width
                  verticalAlign: 'top',
                }}
              >
                <h4 style={{ margin: '0 0 4px 0' }}>{activity.name}</h4>
                <p style={{ margin: 0 }}><strong>Category: </strong>{activity.category}</p>
                <p style={{ margin: 0 }}><strong>Distance: </strong>{activity.distance} miles</p>
                <p style={{ margin: 0 }}><strong>Rating: </strong>{activity.rating}</p>
                <Button
                  onClick={() => handleSelectActivity(activity)}
                  variant="contained"
                  size="small"
                  style={{ marginTop: '0.5rem' }}
                >
                  Select
                </Button>
              </div>
            ))}
        </div>
      </div>
    </div>
  );
};

export default FindActivitiesPage;