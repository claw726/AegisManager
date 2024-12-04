// Icon mappings for different file types
export const FILE_ICONS = {
  // Documents
  "application/pdf": {
    icon: "fas fa-file-pdf",
    color: "text-red-600",
  },
  "application/msword": {
    icon: "fas fa-file-word",
    color: "text-blue-600",
  },
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document": {
    icon: "fas fa-file-word",
    color: "text-blue-600",
  },
  "application/vnd.ms-excel": {
    icon: "fas fa-file-excel",
    color: "text-green-600",
  },
  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": {
    icon: "fas fa-file-excel",
    color: "text-green-600",
  },
  "application/vnd.ms-powerpoint": {
    icon: "fas fa-file-powerpoint",
    color: "text-orange-600",
  },
  "application/vnd.openxmlformats-officedocument.presentationml.presentation": {
    icon: "fas fa-file-powerpoint",
    color: "text-orange-600",
  },

  // Images
  "image/jpeg": {
    icon: "fas fa-file-image",
    color: "text-purple-600",
  },
  "image/png": {
    icon: "fas fa-file-image",
    color: "text-purple-600",
  },
  "image/gif": {
    icon: "fas fa-file-image",
    color: "text-purple-600",
  },
  "image/svg+xml": {
    icon: "fas fa-file-image",
    color: "text-purple-600",
  },
  "image/webp": {
    icon: "fas fa-file-image",
    color: "text-purple-600",
  },

  // Audio
  "audio/mpeg": {
    icon: "fas fa-file-audio",
    color: "text-yellow-600",
  },
  "audio/wav": {
    icon: "fas fa-file-audio",
    color: "text-yellow-600",
  },
  "audio/midi": {
    icon: "fas fa-file-audio",
    color: "text-yellow-600",
  },

  // Video
  "video/mp4": {
    icon: "fas fa-file-video",
    color: "text-pink-600",
  },
  "video/quicktime": {
    icon: "fas fa-file-video",
    color: "text-pink-600",
  },
  "video/x-msvideo": {
    icon: "fas fa-file-video",
    color: "text-pink-600",
  },

  // Archives
  "application/zip": {
    icon: "fas fa-file-archive",
    color: "text-amber-600",
  },
  "application/x-rar-compressed": {
    icon: "fas fa-file-archive",
    color: "text-amber-600",
  },
  "application/x-7z-compressed": {
    icon: "fas fa-file-archive",
    color: "text-amber-600",
  },

  // Code
  "text/html": {
    icon: "fas fa-file-code",
    color: "text-indigo-600",
  },
  "text/css": {
    icon: "fas fa-file-code",
    color: "text-indigo-600",
  },
  "application/javascript": {
    icon: "fas fa-file-code",
    color: "text-indigo-600",
  },
  "application/json": {
    icon: "fas fa-file-code",
    color: "text-indigo-600",
  },

  // Text
  "text/plain": {
    icon: "fas fa-file-alt",
    color: "text-gray-600",
  },
  "application/vnd.sketchup.skp": {
    icon: "fas fa-cube",
    color: "text-red-500",
  },
  "application/x-skp": {
    icon: "fas fa-cube",
    color: "text-red-500",
  },
  "application/skp": {
    icon: "fas fa-cube",
    color: "text-red-500",
  },

  // Default
  default: {
    icon: "fas fa-file",
    color: "text-gray-600",
  },
};

// Helper functions to get icon and color
export const getFileIcon = (fileType) => {
  // Check for generic types first
  if (fileType.startsWith("image/")) {
    return FILE_ICONS["image/jpeg"].icon;
  }
  if (fileType.startsWith("video/")) {
    return FILE_ICONS["video/mp4"].icon;
  }
  if (fileType.startsWith("audio/")) {
    return FILE_ICONS["audio/mpeg"].icon;
  }

  return FILE_ICONS[fileType]?.icon || FILE_ICONS["default"].icon;
};

export const getFileColor = (fileType) => {
  // Check for generic types first
  if (fileType.startsWith("image/")) {
    return FILE_ICONS["image/jpeg"].color;
  }
  if (fileType.startsWith("video/")) {
    return FILE_ICONS["video/mp4"].color;
  }
  if (fileType.startsWith("audio/")) {
    return FILE_ICONS["audio/mpeg"].color;
  }

  return FILE_ICONS[fileType]?.color || FILE_ICONS["default"].color;
};

// Helper function to get both icon and color
export const getFileIconProperties = (fileType) => {
  return {
    icon: getFileIcon(fileType),
    color: getFileColor(fileType),
  };
};

// Categories for grouping files
export const FILE_CATEGORIES = {
  DOCUMENT: "document",
  IMAGE: "image",
  AUDIO: "audio",
  VIDEO: "video",
  ARCHIVE: "archive",
  CODE: "code",
  TEXT: "text",
  MODEL_3D: "3d_model",
  OTHER: "other",
};

// Helper function to get file category
export const getFileCategory = (fileType) => {
  if (fileType.startsWith("image/")) return FILE_CATEGORIES.IMAGE;
  if (fileType.startsWith("video/")) return FILE_CATEGORIES.VIDEO;
  if (fileType.startsWith("audio/")) return FILE_CATEGORIES.AUDIO;
  if (
    fileType.includes("pdf") ||
    fileType.includes("word") ||
    fileType.includes("excel") ||
    fileType.includes("powerpoint")
  ) {
    return FILE_CATEGORIES.DOCUMENT;
  }
  if (
    fileType.includes("zip") ||
    fileType.includes("rar") ||
    fileType.includes("7z")
  ) {
    return FILE_CATEGORIES.ARCHIVE;
  }
  if (
    fileType.includes("html") ||
    fileType.includes("css") ||
    fileType.includes("javascript") ||
    fileType.includes("json")
  ) {
    return FILE_CATEGORIES.CODE;
  }
  if (fileType.includes("text/")) return FILE_CATEGORIES.TEXT;
  if (fileType.includes("skp") || fileType.includes("sketchup")) {
    return FILE_CATEGORIES.MODEL_3D;
  }

  return FILE_CATEGORIES.OTHER;
};
