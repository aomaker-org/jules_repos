import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation

# 1. SETUP THE PLOT
# ------------------
fig, ax = plt.subplots(figsize=(5.5, 5.5))
BACKGROUND_COLOR = '#090909'
fig.patch.set_facecolor(BACKGROUND_COLOR)
ax.set_facecolor(BACKGROUND_COLOR)

# 2. SETUP THE CALCULATION PARAMETERS
# -----------------------------------
num_points = 10000
i = np.arange(num_points - 1, -1, -1)
x = i
y = i / 235.0

# 3. DEFINE THE ANIMATION UPDATE FUNCTION
# ---------------------------------------
def update_frame(t):
    """Calculates point positions and redraws the plot for a given time 't'."""
    ax.clear()

    # --- Mathematical Formulas ---
    k = (4 + np.sin(x/11 + 8*t)) * np.cos(x/14)
    e = y/8 - 19
    d = np.sqrt(k**2 + e**2) + np.sin(y/9 + 2*t)
    q = 2*np.sin(2*k) + np.sin(y/17)*k*(9 + 2*np.sin(y - 3*d))
    c = d**2/49 - t
    xp = q + 50*np.cos(c) + 200
    yp = q*np.sin(c) + d*39 - 440
    final_y = 400 - yp

    # --- Plotting ---
    ax.scatter(xp, final_y, s=0.01, c='white', alpha=0.9)

    # --- Style the plot ---
    ax.set_facecolor(BACKGROUND_COLOR)
    ax.set_xlim(70, 330)
    ax.set_ylim(30, 350)
    ax.axis('off')

    return ax,

# 4. CREATE THE ANIMATION
# -------------------------------
t_values = np.linspace(0, 2 * np.pi, 180)

ani = animation.FuncAnimation(
    fig=fig,
    func=update_frame,
    frames=t_values,
    interval=30,
    blit=True
)

# 5. SAVE THE ANIMATION TO A FILE
# -------------------------------
# Instead of plt.show(), use ani.save().
# You must have FFmpeg installed for this to work.

print("Saving animation as MP4... This may take a moment.")
ani.save(
    'generative_art.mp4',
    writer='ffmpeg',
    fps=30,
    dpi=100 # Adjust DPI for higher or lower resolution
)
print("Animation saved as generative_art.mp4")

# To save as a GIF instead, you can use:
# print("Saving animation as GIF...")
# ani.save('generative_art.gif', writer='pillow', fps=30)
# print("Animation saved as generative_art.gif")

# plt.show() # Keep this commented out when saving a file.
