# 2048
Game 2048.
1.In each round, a tile with a face value of '2' (with a probability of 90%) or '4' (with a probability of 10%) appears [11]
2.By pressing the arrow, the player can throw off all the tiles of the playing field in one of 4 directions. 
If, when dropped, two tiles of the same denomination “bump” one onto the other, then they turn into one, 
the denomination of which is equal to the sum of the connected tiles. 
After each move, a new tile with a value of '2' or '4' appears on the free section of the field. 
If, when the button is pressed, the location of the tiles or their value does not change, then the move is not made.
3.If there are more than two tiles of the same denomination in one line or in one column, then when dropped, 
they begin to connect from the side they were directed to. For example, 
tiles (4, 4, 4) in the same row will turn into (8, 4) after a move to the left, and into (4, 8) after a move to the right. 
This processing of ambiguity allows you to more accurately form the strategy of the game.
4.For each connection, game points are increased by the face value of the resulting tile.
5.The game ends in defeat if after the next move it is impossible to take an action.
