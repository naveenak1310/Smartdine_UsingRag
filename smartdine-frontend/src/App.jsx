import { Routes, Route, Link, useLocation } from 'react-router-dom'
import Home from './pages/Home'
import Search from './pages/Search'
import Result from './pages/Result'
import Login from './pages/Login'
import Signup from './pages/Signup'
import Contact from './pages/Contact'
import RestaurantDetails from "./pages/RestaurantDetails"; 
import Booking from "./pages/Booking";
import History from "./pages/History";
import Wishlist from "./pages/Wishlist";
import Admin from './pages/Admin'

import Chatbot from "./pages/Chatbot";  
import Lottie from "lottie-react";
import serveFoodAnim from './assets/ServeFood.json';
import "./App.css";

function App() {
  const location = useLocation()

  const currentUser = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;
  const isAdmin = currentUser && currentUser.email === 'admin@smartdine.com';

  return (
    <div className="app-root">
      <header className="app-header">
        <Link to="/" className="logo">
          <Lottie animationData={serveFoodAnim} loop={false} style={{ width: 50, height: 50 }} />
          <span>SmartDine</span>
        </Link>

        <nav className="nav-links">
          <Link to="/search" className={location.pathname === '/search' ? 'active' : ''}>
            Search
          </Link>

          {!localStorage.getItem("loggedIn") ? (
            <>
              <Link to="/login" className={location.pathname === '/login' ? 'active' : ''}>
                Login
              </Link>
              <Link to="/signup" className={location.pathname === '/signup' ? 'active' : ''}>
                Signup
              </Link>
            </>
          ) : (
            <button
              className="logout-btn"
              onClick={() => {
              localStorage.removeItem("loggedIn");
              localStorage.removeItem("user");
              localStorage.removeItem("isAdmin");
              window.location.href = "/login";
              }}

            >
              Logout
            </button>
          )}

          <Link to="/contact" className={location.pathname === '/contact' ? 'active' : ''}>
            Contact
          </Link>
          
          {localStorage.getItem("loggedIn") ? (
            <>
              <Link to="/wishlist" className={location.pathname === '/wishlist' ? 'active' : ''}>
                 Wishlist
              </Link>
              <Link to="/history" className={location.pathname === '/history' ? 'active' : ''}>
                History
              </Link>
              {isAdmin ? (
                <Link to="/admin" className={location.pathname === '/admin' ? 'active' : ''}>
                  Admin
                </Link>
              ) : null}
            </>
          ) : null}
      
        </nav>
      </header>

      <main className="app-main">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/search" element={<Search />} />
          <Route path="/result" element={<Result />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/contact" element={<Contact />} />

          <Route path="/restaurant/:id" element={<RestaurantDetails />} />
          <Route path="/booking/:id" element={<Booking />} />
          <Route path="/history" element={<History />} />
          <Route path="/wishlist" element={<Wishlist />} />

          <Route path="/admin" element={<Admin />} />
        </Routes>
      </main>

     
      <Chatbot />

      <footer className="app-footer">
        <span>SmartDine · simple suggestions for confused foodies</span>
      </footer>
    </div>
  )
}

export default App;
