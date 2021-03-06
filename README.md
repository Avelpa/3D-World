# 3D-World

[3D world video](https://www.youtube.com/watch?v=mkxxVxicPyM&feature=youtu.be)

[3D conway video](https://www.youtube.com/watch?v=VkcN8OfD-xs&feature=youtu.be)


## Quick Overview
- The 3D environment (cameras, lighting, ...) is made using linear algebra (no 3D libraries)
- World can be rendered from multiple locations (i.e., multiple cameras)
- Player can move around and place down various polyhedrons (cubes, pyramids) or they can create their own by joining points and extending surfaces
- Lights can be added to the environment
- Player can collide with objects in the environment


### This project was initially created to make [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway's_Game_of_Life "Wikipedia") in **3D**.
### However, the achieved effect was underwhelming, as the spawned cells either grew/died too quickly, and when they did grow they became  a big blob that lagged out the simulation.
### So instead the focus of the project was turned to making a 3D **_world_** in which 3D objects and lights could be placed.

## So the _Conway_ version of this project is in the `main` branch, and the _3D World_ version is in the `3D non-conway` branch.

The **3D** effect was achieved by creating representations for 3D vectors/matrices and using them in various mathematical manipulations such as vector projections onto other vectors & planes, ...

[3D World](#3d-world)  
[Conway 3D](#conway-3d)

## 3D-World:
### Controls:

#### General
Key | Function
--- | ---
`Esc` | Toggles in-game mode

#### In-game mode (i.e., you can control the player)
  In-game mode has two sub-modes: spectator and non-spectator
   - In spectator mode, the player does not fall and cannot collide with objects
   - In non-spectator mode, the opposite is true
  Non in-game mode has a red border surrounding the screen
   
Key | Function
--- | ---
`Mouse movement` | Look around
`Left-click` | Place down object
`Right-click` | De-select point/polygon
`Middle-click (scroll wheel)` | Select object's face (polygon)
`Ctrl` | Toggles spectator mode
`Down arrow` | If a polygon is selected, extends that polygon to the player's cursor 
`WASD` | Move forward,back,left,right respectively
`Space` | Spectator mode: fly up. Non-spectator mode: jump up
`Capslock` | Spectator mode: fly down

#### Not in-game mode
Key | Function
--- | ---
`Space` | Opens up object-selection menu
`Left/Right arrows` | Cycles through objects in the object-selection menu
`Enter` | Select current object in the object-selection menu

### Object Drawing
- When creating free-drawn objects, connect a point to another point to close the surface.
- If a point on a surface is selected, any subsequent point spawned will be co-planar to that surface

## Conway 3D:
Note: this version uses an old version of the camera and spectator classes, giving the player the ability to flip upside down (and thus make the movement weirdly reversed) among other issues that were removed for the **3D World** project.

This version has a dual-camera set-up. Switch between cameras by pressing `L` in the in-game mode.

The simulation only runs in in-game mode, and can be toggled by pressing `S` in the non in-game mode.

#### General
Key | Function
--- | ---
`Esc` | Toggles in-game mode

#### Not in-game mode
Key | Function
--- | ---
`S` | Toggles the Conway simulation

#### In-game mode (i.e., you can control the player)

##### Drawing
- Dots can only be spawned on the current target (black circle), which snaps to a 2x2x2 meter grid

Key | Function
--- | ---
`Mouse movement` | Look around
`Left-click` | Spawn dot at target
`WASD` | Move forward,back,left,right respectively
`Space` | Move up
`Capslock` | Move down
`L` | Switch between camera1 and camera2
