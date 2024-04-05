// SPDX-License-Identifier: GPL-2.0-or-later

#pragma once

#ifdef __OBJC__
#import <Metal/Metal.h>
#import <QuartzCore/QuartzCore.h>
typedef id<MTLDevice> MTLDevice_t;
typedef id<MTLCommandQueue> MTLCommandQueue_t;
typedef id<MTLCommandBuffer> MTLCommandBuffer_t;
typedef id<MTLTexture> MTLTexture_t;
typedef id<CAMetalDrawable> CAMetalDrawable_t;
#else
typedef void* MTLDevice_t;
typedef void* MTLCommandQueue_t;
typedef void* MTLCommandBuffer_t;
typedef void* MTLTexture_t;
typedef void CAMetalLayer;
typedef void* CAMetalDrawable_t;
#endif
