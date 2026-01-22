import React, { createContext, ReactNode, useContext, useEffect, useState } from 'react'
const BASE_URL = "http://localhost:8080";

interface AuthContextType{
    token: string | null;
    setToken: (token: string | null) => void;
    logout: () => void;
    loading: boolean;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({children} : {children: ReactNode}) => {
    const [token, setToken] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        const checkAuth = async () => {
            try{
                const res = await fetch(BASE_URL + '/auth/me', {
                    credentials: 'include'
                })
                if(res.ok){
                    const data = await res.json();
                    setToken(data.username);
                    console.log("Token set to:", data.username);
                }
            } catch(err) {
                throw new Error("Error checking auth: " + err);
            } finally {
                setLoading(false);
            }
        }
        checkAuth();
    }, [])

    const logout = () => {
        setToken(null);
    };

    return(
        <AuthContext.Provider value={{token, setToken, logout, loading}}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => {
    const context = useContext(AuthContext);
    if(!context) throw new Error('useAuth must be used in AuthProvider Object');
    return context;
}