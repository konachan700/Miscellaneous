
PCD8544_VERSION = master
PCD8544_SITE_METHOD=git
PCD8544_SITE = git://github.com/konachan700/PCD8544-linux

define PCD8544_BUILD_CMDS
	$(TARGET_MAKE_ENV) $(MAKE) CC="$(TARGET_CC)" LD="$(TARGET_LD)" -C $(@D) all
endef

define PCD8544_INSTALL_TARGET_CMDS
	$(TARGET_MAKE_ENV) $(MAKE) -C $(@D) install DESTDIR=$(TARGET_DIR)
endef

$(eval $(generic-package))
