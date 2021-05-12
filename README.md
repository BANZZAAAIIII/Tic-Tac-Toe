# Tic-Tac-Toe

En enkel multiplayer 3 på rad app som lar deg spille med andre via [GenericGameService](https://github.com/crismo/GenericGameService) fra crismo

GenericGameService har en veldig enkel spill-tjeneste API. Den lager en type lobby som kan oppdateres med en state. Denne staten kan være hva som helst, men i denne appen brukt til 3 på rad.

Tic-Tac-Toe er bygget opp som en single-activity app med 2 fragemnter, en for hovedmenyen og en for spillet. Det er også brukt to BottomSheetDialogFragment for å lage og joine et game. Navigasjon mellom fragmenten er gjort via navigation component og overfører data med Safe Args. 

Appen er or også bygget opp med klare abstraksjons nivåer. Hvor fragmentene er views som skal inneholde så lite logikk som mulig og da bare vise og sende data mellom GameMananger. 
GameManager er dermed ansvarlig for spill logikken og oppdatering av spillets view via livedate. GameManager oppdatere også server via GameService. 
GameService sin oppgave er bare å kommunisere med spill serveren via GenericGameService's API. 

Oppsummert er det GameManager som bestemmer hva som skal skje når spilleren gjør noe i vieww'et og GameManager bestemmer når GameState skal bli lastet opp og ned via GameService.

### Bilder av appen
![](https://user-images.githubusercontent.com/69724523/117991001-f59cc100-b33d-11eb-8251-001e6833efdb.png) 
![](https://user-images.githubusercontent.com/69724523/117991114-0cdbae80-b33e-11eb-857e-32f60f332dc5.png)
