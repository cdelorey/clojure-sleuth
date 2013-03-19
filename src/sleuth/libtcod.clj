;TODO: can default values be added to functions so root does not need to be passed 
; so much?
;TODO: add event enum
;TODO: write wrapper around console pring so a map of fg and bg color can be passed in
(ns sleuth.libtcod
  (:use [clj-native.direct :only [defclib loadlib typeof]]
        [clj-native.structs :only [byref byval]]
        [clj-native.callbacks :only [callback]]))


; Enums
; Defining enum values as constant vars for now.
;------------------------------------------------------------------------------------
; TCOD_renderer_t
(def ^:const tcod-renderer-glsl 0)
(def ^:const tcod-renderer-opengl 1)
(def ^:const tcod-renderer-sdl 2)
(def ^:const tcod-nb-renderers 3)

; TCOD_alignment_t
(def ^:const tcod-left 0) 
(def ^:const tcod-right 1) 
(def ^:const tcod-center 2)

; TCOD_custom_font_flags
(def ^:const font-layout-ascii-in-col   1)
(def ^:const font-layout-ascii-in-row   2)
(def ^:const font-type-greyscale        4)
(def ^:const font-layout-tcod           8)

; TCOD_colctrl_t
(def ^:const tcod-colctrl-1 1)
(def ^:const tcod-colctrl-2 2)
(def ^:const tcod-colctrl-3 3)
(def ^:const tcod-colctrl-4 4)
(def ^:const tcod-colctrl-5 5)
(def ^:const tcod-colctrl-number 5)
(def ^:const tcod-colctrl-fore-rgb 6)
(def ^:const tcod-colctrl-back-rgb 7)
(def ^:const tcod-colctrl-stop 8)

; TCOD_bkgnd_flag_t
(def ^:const tcod-bkgnd-none 0)
(def ^:const tcod-bkgnd-set 1)
(def ^:const tcod-bkgnd-multiply 2)
(def ^:const tcod-bkgnd-lighten 3)
(def ^:const tcod-bkgnd-darken 4)
(def ^:const tcod-bkgnd-screen 5)
(def ^:const tcod-bkgnd-color-dodge 6)
(def ^:const tcod-bkgnd-color-burn 7)
(def ^:const tcod-bkgnd-add 8)
(def ^:const tcod-bkgnd-adda 9)
(def ^:const tcod-bkgnd-burn 10)
(def ^:const tcod-bkgnd-overlay 11)
(def ^:const tcod-bkgnd-alph 12)
(def ^:const tcod-bkgnd-default 13)

; TCOD_key_status_t
(def ^:const tcod-key-pressed 1)
(def ^:const tcod-key-released 2)

; TCOD_keycode_t
; these may need to be rearranged
(def ^:const key-none 0)
(def ^:const key-escape 1)
(def ^:const key-backspace 2)
(def ^:const key-tab 3)
(def ^:const key-enter 4)
(def ^:const key-shift 5)
(def ^:const key-control 6)
(def ^:const key-alt 7)
(def ^:const key-pause 8)
(def ^:const key-capslock 9)
(def ^:const key-pageup 10)
(def ^:const key-pagedown 11)
(def ^:const key-end 12)
(def ^:const key-home 13)
(def ^:const key-up 14)
(def ^:const key-left 15)
(def ^:const key-right 16)
(def ^:const key-down 17)
(def ^:const key-printscreen 18)
(def ^:const key-insert 19)
(def ^:const key-delete 20)
(def ^:const key-lwin 21)
(def ^:const key-rwin 22)
(def ^:const key-apps 23)
(def ^:const key-0 24)
(def ^:const key-1 25)
(def ^:const key-2 26)
(def ^:const key-3 27)
(def ^:const key-4 28)
(def ^:const key-5 29)
(def ^:const key-6 30)
(def ^:const key-7 31)
(def ^:const key-8 32)
(def ^:const key-9 33)
(def ^:const key-kp0 34)
(def ^:const key-kp1 35)
(def ^:const key-kp2 36)
(def ^:const key-kp3 37)
(def ^:const key-kp4 38)
(def ^:const key-kp5 39)
(def ^:const key-kp6 40)
(def ^:const key-kp7 41)
(def ^:const key-kp8 42)
(def ^:const key-kp9 43)
(def ^:const key-kpadd 44)
(def ^:const key-kpsub 45)
(def ^:const key-kpdiv 46)
(def ^:const key-kpmul 47)
(def ^:const key-kpdec 48)
(def ^:const key-kpenter 49)
(def ^:const key-f1 50)
(def ^:const key-f2 51)
(def ^:const key-f3 52)
(def ^:const key-f4 53)
(def ^:const key-f5 54)
(def ^:const key-f6 55)
(def ^:const key-f7 56)
(def ^:const key-f8 57)
(def ^:const key-f9 58)
(def ^:const key-f10 59)
(def ^:const key-f11 60)
(def ^:const key-f12 61)
(def ^:const key-numlock 62)
(def ^:const key-scrollock 63)
(def ^:const key-space 64)
(def ^:const key-char 65)

; TCOD_chars_t
; single walls 
(def ^:const char-hline 196)
 (def ^:const char-vline 179)	
 (def ^:const char-ne 191)
 (def ^:const char-nw 218)
 (def ^:const char-se 217)
 (def ^:const char-sw 192)
 (def ^:const char-teew 180)
 (def ^:const char-teee 195)
 (def ^:const char-teen 193)
 (def ^:const char-tees 194)
 (def ^:const char-cross 197)
; double walls 
 (def ^:const char-dhline 205)
 (def ^:const char-dvline 186)
 (def ^:const char-dne 187)
 (def ^:const char-dnw 201)
 (def ^:const char-dse 188)
 (def ^:const char-dsw 200)
 (def ^:const char-dteew 185)
 (def ^:const char-dteee 204)
 (def ^:const char-dteen 202)
 (def ^:const char-dtees 203)
 (def ^:const char-dcross 206)
; blocks 
 (def ^:const char-block1 176)
 (def ^:const char-block2 177)
 (def ^:const char-block3 178)
; arrows 
 (def ^:const char-arrow-n 24)
 (def ^:const char-arrow-s 25)
 (def ^:const char-arrow-e 26)
 (def ^:const char-arrow-w 27)
; arrows without tail 
 (def ^:const char-arrow2-n 30)
 (def ^:const char-arrow2-s 31)
 (def ^:const char-arrow2-e 16)
 (def ^:const char-arrow2-w 17)
; double arrows 
 (def ^:const char-darrow-h 29)
 (def ^:const char-darrow-v 18)
; gui stuff 
 (def ^:const char-checkbox-unset 224)
 (def ^:const char-checkbox-set 225)
 (def ^:const char-radio-unset 9)
 (def ^:const char-radio-set 10)
; sub-pixel resolution kit 
 (def ^:const char-subp-nw 226)
 (def ^:const char-subp-ne 227)
 (def ^:const char-subp-n 228)
 (def ^:const char-subp-se 229)
 (def ^:const char-subp-diag 230)
 (def ^:const char-subp-e 231)
 (def ^:const char-subp-sw 232)
; miscellaneous
 (def ^:const char-smilie   1)
 (def ^:const char-smilie-inv   2)
 (def ^:const char-heart   3)
 (def ^:const char-diamond   4)
 (def ^:const char-club   5)
 (def ^:const char-spade   6)
 (def ^:const char-bullet   7)
 (def ^:const char-bullet-inv   8)
 (def ^:const char-male   11)
 (def ^:const char-female   12)
 (def ^:const char-note   13)
 (def ^:const char-note-double   14)
 (def ^:const char-light   15)
 (def ^:const char-exclam-double   19)
 (def ^:const char-pilcrow   20)
 (def ^:const char-section   21)
 (def ^:const char-pound   156)
 (def ^:const char-multiplication   158)
 (def ^:const char-function   159)
 (def ^:const char-reserved   169)
 (def ^:const char-half   171)
 (def ^:const char-one-quarter   172)
 (def ^:const char-copyright   184)
 (def ^:const char-cent   189)
 (def ^:const char-yen   190)
 (def ^:const char-currency   207)
 (def ^:const char-three-quarters   243)
 (def ^:const char-division   246)
 (def ^:const char-grade   248)
 (def ^:const char-umlaut   249)
 (def ^:const char-pow1   251)
 (def ^:const char-pow3   252)
 (def ^:const char-pow2   253)
 (def ^:const char-bullet-square   254)
 
; Color names
(def ^:const tcod-color-red 0)
(def ^:const tcod-color-flame 1)
(def ^:const tcod-color-orange 2)
(def ^:const tcod-color-amber 3)
(def ^:const tcod-color-yellow 4)
(def ^:const tcod-color-lime 5)
(def ^:const tcod-color-chartreuse 6)	
(def ^:const tcod-color-gree 7)
(def ^:const tcod-color-sea 8)
(def ^:const tcod-color-turquoise 9)
(def ^:const tcod-color-cyan 10)
(def ^:const tcod-color-sky 11)
(def ^:const tcod-color-azure 12)
(def ^:const tcod-color-blue 13)
(def ^:const tcod-color-han 14)
(def ^:const tcod-color-violet 15)
(def ^:const tcod-color-purple 16)
(def ^:const tcod-color-fuchsia 17)
(def ^:const tcod-color-magenta 18)
(def ^:const tcod-color-pink 19)
(def ^:const tcod-color-crimson 20)
(def ^:const tcod-color-nb 21)

; Constants
(def root nil)

; Library definition -----------------------------------------------------------------
(defclib
  libtcod 
  (:libname "tcod_debug")
  (:structs
   (key-t :vk int :c char :lalt bool :lctrl bool :ralt bool :rctrl bool :shift bool)
   (mouse-t :x int :y int :dx int :dy int :cx int :cy int :dcx int :dcy int 
            :lbutton bool :rbutton bool :mbutton bool :lbutton-pressed bool
            :rbutton-pressed bool :mbutton-pressed bool :wheel-up bool :wheel-down bool)
   (color-t :r int :g int :b int))
  (:unions)
  (:callbacks)
  (:functions
   ; Console module -------------------------------------------------------------
   (console-init-root TCOD_console_init_root [int int constchar* bool enum] void)
   (console-set-window-title TCOD_console_set_window_title [constchar*] void)
   (console-set-fullscreen TCOD_console_set_fullscreen [bool] void)
   (console-is-fullscreen? TCOD_console_is_fullscreen [] bool) 
   (console-is-window-closed? TCOD_console_is_window_closed [] bool)
   (console-has-mouse-focus? TCOD_console_has_mouse_focus [] bool)
   (console-is-active? TCOD_console_is_active [] bool)
   
   
   (console-set-custom-font TCOD_console_set_custom_font 
                            [constchar*  int int int] void)
   
   
   (console-set-default-background TCOD_console_set_default_background
                                   [void* color-t] void)
   (console-set-default-foreground TCOD_console_set_default_foreground
                                   [void* color-t] void)
   (console-clear TCOD_console_clear [void*] void)
   (console-set-char-background TCOD_console_set_char_background 
                                [void* int int color-t enum])
   (console-set-char-foreground TCOD_console_set_char_foreground 
                                [void* int int color-t])
   (console-set-char TCOD_console_set_char [void* int int int] void)
   (console-put-char TCOD_console_put_char [void* int int int enum] void)
   (console-put-char-ex TCOD_console_put_char_ex [void* int int int color-t color-t] void)
   
   
   (console-get-width TCOD_console_get_width [void*] int)
   (console-get-height TCOD_console_get_height [void*] int)
   (console-get-default-background TCOD_console_get_default_background [void*] color-t)
   (console-get-default-foreground TCOD_console_get_default_foreground [void*] color-t)
   (console-get-char TCOD_console_get_char [void* int int] int)
   
   
   (console-set-fade TCOD_console_set_fade [int color-t] void)
   (console-get-fade TCOD_console_get_fade [] int)
   (console-get-fading-color TCOD_console_get_fading_color [] color-t)
   
   (console-print TCOD_console_print [void* int int constchar*] void)
   (console-print-rect TCOD_console_print_rect [void* int int int int constchar*] int)
   (console-rect TCOD_console_rect [void* int int int int bool enum] void)
   (console-hline TCOD_console_hline [void* int int int enum] void)
   (console-vline TCOD_console_vline [void* int int int enum] void)
   (console-print-frame TCOD_console_print_frame 
                        [void* int int int int bool enum constchar*])
   
   (console-flush TCOD_console_flush [] void)
   
   
   (console-wait-for-keypress TCOD_console_wait_for_keypress [bool] key-t)
   (console-check-for-keypress TCOD_console_check_for_keypress [enum] key-t)
   (console-is-key-pressed? TCOD_console_is_key_pressed [enum] bool)
   (console-wait-for-event TCOD_sys_wait_for_event [int key-t mouse-t bool] enum)
   (console-check-for-event TCOD_sys_check_for_event [enum key-t mouse-t] enum)
   
   ; Color module --------------------------------------------------------------
   (tcod-color-rgb TCOD_color_RGB [int int int] color-t)
   (tcod-color-hsv TCOD_color_HSV [float float float] color-t)
   
   
   ; Mouse module --------------------------------------------------------------
   (mouse-show-cursor TCOD_mouse_show_cursor [bool] void)
   (mouse-is-cursor-visible TCOD_mouse_is_cursor_visible [] bool)
   (mouse-move TCOD_mouse_move [int int] void)
   (mouse-get-status TCOD_mouse_get_status [] mouse-t)
    
   ))

; Wrapper functions ------------------------------------------------------------
(defn color-rgb
  "Allow passing of arguments to color functions as individual numbers or as a vector."
  ([[i j k]]
   (tcod-color-rgb i j k))
  ([i j k]
   (tcod-color-rgb i j k)))

(defn color-hsv
  ([[i j k]]
   (tcod-color-hsv i j k))
  ([i j k]
   (tcod-color-hsv i j k)))

;test
(defn libtcod-test
  []
  (loadlib libtcod)
  (console-set-custom-font "terminal.png" font-layout-ascii-in-col 16 16)
  (console-init-root 80 25 "Test" false tcod-renderer-sdl)
  (console-set-default-background root (color-rgb 100 30 35))
  (console-set-default-foreground root (color-rgb 0 0 0))
  (console-clear root)
  (console-set-char-background root 10 10 (color-rgb 10 45 11) tcod-bkgnd-set)
  (console-set-char-foreground root 10 10 (color-rgb 0 0 0))
  (console-set-char root 10 10 char-block3)
  (console-set-char root 10 11 char-block3)
  (console-set-char root 11 10 char-block3)
  (console-put-char root 15 15 char-smilie tcod-bkgnd-set)
  (console-put-char-ex root 15 20 char-dhline 
                       (color-rgb 1 45 100) (color-rgb 100 100 100))
  (console-print root 1 1 "Hello, World!")
  (mouse-move 50 50)
  (console-flush)
  (println (console-is-fullscreen?))
  (println (console-is-window-closed?))
  (println (console-has-mouse-focus?))
  (println (console-is-active?))
  (println (console-get-char root 10 10))
  (println (console-get-default-background root))
  (console-wait-for-keypress true)
  (console-set-default-background root (color-rgb 255 10 170))
  (console-rect root 10 10 10 10 true tcod-bkgnd-set)
  (console-hline root 5 5 30 tcod-bkgnd-none)
  (console-vline root 5 5 10 tcod-bkgnd-set)
  (console-print-frame root 5 5 20 10 true tcod-bkgnd-none nil)
  (console-print-rect root 5 5 20 10 "fjjasdkljaskldjaklsjdklasjdklasjdlkasjdlkjasd")
  (console-flush)
  (console-wait-for-keypress true))
  


 