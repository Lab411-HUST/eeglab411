/* C */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <locale.h>
#include <errno.h>

/* Unix */
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <sys/utsname.h>
#include <fcntl.h>
#include <poll.h>

/* Linux */
#include <linux/version.h>
#include <linux/input.h>

/*Android Log*/
#include <android/log.h>

#define HID_MAX_DESCRIPTOR_SIZE		4096

#define  LOG_TAG    "NDK-hp"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

struct hidraw_report_descriptor{
	__u32 size;
	__u8 value[HID_MAX_DESCRIPTOR_SIZE];
};

struct hidraw_devinfo{
	__u32 bustype;
	__s16 vendor;
	__s16 product;
};

struct hid_device_{
	int device_handle;
	int blocking;
	int uses_numbered_reports;
};

typedef struct hid_device_ hid_device;
static __u32 kernel_version = 0;

/*C function define*/
static __u32 detect_kernel_version(void);
static int uses_numbered_reports(__u8 *report_descriptor, __u32 size);
static hid_device *new_hid_device(void);
int hid_init(void);
int hid_read_timeout(hid_device *dev, unsigned char *data, size_t length, int milliseconds);
int hid_read(hid_device *dev, unsigned char *data, size_t length);
void print_usage(void);
void print_dev(hid_device *dev);
void print_dev_info(struct hidraw_devinfo *dev);
hid_device *hid_open(const char *path_dev);
