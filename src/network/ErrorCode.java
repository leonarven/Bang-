package network;

public enum ErrorCode {
	SERVER_IS_FULL,		// Uusia clienttejä ei huolita
	INVALID_PACKET,   	// Virheellinen paketti
	
	ILLEGAL_MOVE,      	// Laiton siirto
	ILLEGAL_END,       	// Laiton vuoron lopetus (Liikaa kortteja)
	INVALID_CARD;    	// Puhe vääränlaisesta kortista / “siulle ei oo
	
    public static ErrorCode fromInt(int x) {
        switch(x) {
        
        case 1: return SERVER_IS_FULL;
        case 2: return INVALID_PACKET;
        
        case 101: return ILLEGAL_MOVE;
        case 102: return ILLEGAL_END;
        case 103: return INVALID_CARD;

        default: return null;
        }
    }
}
