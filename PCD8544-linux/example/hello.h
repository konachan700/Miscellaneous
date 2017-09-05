#define SHARED_MEMORY_OBJECT_NAME   "pcd8544_disp_struct"

struct pcd8544_disp_struct {
    int magic;
    char text[6][14];
    unsigned char line_0_clear;
};
