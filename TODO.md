1. Refactor Card (Figure, Color, Card classes)
1. for starters keep games in cache, assign ID to state and keep it in cache or proper equals method

TODO on the level of server, check if the game is memory effective - if not include Flyweight pattern or oneK.state caching - proper hashing function is needed

TODO add objects being default validators for specific variants e.g. TwoPlayerSummaryValidator - decreases object creation per each game obj creation, or use cached game instances

TODO add random agent for test purposes - with logging, check if acheiving game goal is feasible - maybe lowe the goal
TODO update readme